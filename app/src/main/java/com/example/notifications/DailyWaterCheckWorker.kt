package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.data.GardenDatabase
import com.example.data.model.UserPlantWithDetails
import kotlinx.coroutines.flow.first

class DailyWaterCheckWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "garden_watering_reminders"
        const val CHANNEL_NAME = "تنبيهات ري النباتات"
        const val CHANNEL_DESC = "إشعارات يومية لتذكيرك بالنباتات التي تحتاج إلى ري"
    }

    override suspend fun doWork(): Result {
        return try {
            val db = GardenDatabase.getInstance(appContext)
            val plantDao = db.plantDao()
            val userPlantDao = db.userPlantDao()
            val careLogDao = db.careLogDao()

            val plants = plantDao.getAllPlants().first().associateBy { it.id }
            val userPlants = userPlantDao.getAllUserPlants().first()
            val allCareLogs = careLogDao.getAllCareLogs().first().groupBy { it.user_plant_id }

            val plantsNeedingWater = mutableListOf<UserPlantWithDetails>()

            for (up in userPlants) {
                val basePlant = plants[up.plant_id] ?: continue
                val logs = allCareLogs[up.id].orEmpty()
                val lastWatered = logs.firstOrNull { it.action_type == "watered" }

                val details = UserPlantWithDetails(
                    userPlant = up,
                    plant = basePlant,
                    place = null,
                    lastWateredLog = lastWatered,
                    latestCareLog = logs.firstOrNull()
                )

                if (details.needsWatering) {
                    plantsNeedingWater.add(details)
                }
            }

            if (plantsNeedingWater.isNotEmpty()) {
                sendConsolidatedNotification(plantsNeedingWater)
            } else {
                Log.d("DailyWaterCheckWorker", "No plants need watering today.")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("DailyWaterCheckWorker", "Error executing daily water check", e)
            Result.retry()
        }
    }

    private fun sendConsolidatedNotification(plants: List<UserPlantWithDetails>) {
        createNotificationChannel()

        val count = plants.size
        val plantNames = plants.joinToString("، ") { it.userPlant.nickname }
        val title = if (count == 1) {
            "نبتة واحدة محتاجة ري اليوم 🌱"
        } else {
            "$count نباتات محتاجة ري اليوم 💧"
        }
        val contentText = "$plantNames محتاجة سقاية للحفاظ على نضارتها."

        // Intent to open the app when tapping the notification
        val openAppIntent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Quick action intent: "تم السقي" BroadcastReceiver
        val userPlantIds = plants.map { it.userPlant.id }.toIntArray()
        val waterIntent = Intent(appContext, WaterCareReceiver::class.java).apply {
            action = WaterCareReceiver.ACTION_WATER_PLANTS
            putExtra(WaterCareReceiver.EXTRA_USER_PLANT_IDS, userPlantIds)
        }
        val waterPendingIntent = PendingIntent.getBroadcast(
            appContext,
            1,
            waterIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$contentText\nاضغط \"تم السقي\" لتسجيل الري مباشرة."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_launcher_foreground,
                    "💧 تم السقي",
                    waterPendingIntent
                ).build()
            )
            .build()

        try {
            NotificationManagerCompat.from(appContext).notify(WaterCareReceiver.NOTIFICATION_ID, notification)
            Log.d("DailyWaterCheckWorker", "Sent consolidated watering notification for $count plants")
        } catch (e: SecurityException) {
            Log.e("DailyWaterCheckWorker", "Missing notification permission", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }
            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
