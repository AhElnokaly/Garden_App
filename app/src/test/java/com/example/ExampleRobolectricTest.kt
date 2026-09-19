package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.GardenDatabase
import com.example.data.InitialPlantData
import com.example.data.model.CareLog
import com.example.data.model.Plant
import com.example.data.model.UserPlant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: GardenDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, GardenDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Garden Companion", appName)
    }

    @Test
    fun `room database insert and query plants`() = runBlocking {
        db.plantDao().insertAll(InitialPlantData.initialPlants)
        val plants = db.plantDao().getAllPlants().first()
        assertTrue(plants.size >= 14)

        val mint = plants.firstOrNull { it.name_ar.contains("نعناع") }
        assertNotNull(mint)
        assertEquals("Spearmint", mint?.name_en)
    }

    @Test
    fun `insert user plant and log care actions`() = runBlocking {
        db.plantDao().insertAll(InitialPlantData.initialPlants)
        val plantId = 1

        val userPlant = UserPlant(
            id = 1,
            plant_id = plantId,
            nickname = "نعناع البلكونة",
            place = "البلكونة",
            added_date = System.currentTimeMillis()
        )
        db.userPlantDao().insertUserPlant(userPlant)

        val userPlants = db.userPlantDao().getAllUserPlants().first()
        assertEquals(1, userPlants.size)
        assertEquals("نعناع البلكونة", userPlants[0].nickname)

        // Add care log
        val log = CareLog(
            user_plant_id = userPlant.id,
            action_type = "watered",
            timestamp = System.currentTimeMillis()
        )
        db.careLogDao().insertCareLog(log)

        val logs = db.careLogDao().getCareLogsForPlant(userPlant.id).first()
        assertEquals(1, logs.size)
        assertEquals("watered", logs[0].action_type)
    }
}
