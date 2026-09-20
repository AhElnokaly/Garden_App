package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.data.GardenDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WaterCareReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_WATER_PLANTS = "com.example.gardencompanion.ACTION_WATER_PLANTS"
        const val EXTRA_USER_PLANT_IDS = "extra_user_plant_ids"
        const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_WATER_PLANTS) {
            val plantIds = intent.getIntArrayExtra(EXTRA_USER_PLANT_IDS) ?: intArrayOf()
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = GardenDatabase.getInstance(context)
                    val careLogDao = db.careLogDao()
                    val currentTime = System.currentTimeMillis()

                    for (id in plantIds) {
                        careLogDao.insertCareLog(
                            com.example.data.model.CareLog(
                                user_plant_id = id,
                                action_type = "watered",
                                timestamp = currentTime,
                                note_text = "تم السقي عبر الإشعار السريع 💧"
                            )
                        )
                    }

                    // Cancel the notification after watering is logged
                    val notificationManager = NotificationManagerCompat.from(context)
                    notificationManager.cancel(NOTIFICATION_ID)
                    Log.d("WaterCareReceiver", "Successfully watered ${plantIds.size} plants from notification")
                } catch (e: Exception) {
                    Log.e("WaterCareReceiver", "Error logging water care from notification", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
