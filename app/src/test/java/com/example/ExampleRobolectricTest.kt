package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.GardenDatabase
import com.example.data.GardenRepository
import com.example.data.InitialPlantData
import com.example.data.model.CareLog
import com.example.data.model.ContainerType
import com.example.data.model.DataSource
import com.example.data.model.Garden
import com.example.data.model.GrowingMethod
import com.example.data.model.Place
import com.example.data.model.Plant
import com.example.data.model.PlantContainer
import com.example.data.model.SoilProfile
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
    private lateinit var repository: GardenRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, GardenDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = GardenRepository(
            db.plantDao(),
            db.gardenDao(),
            db.placeDao(),
            db.containerDao(),
            db.userPlantDao(),
            db.careLogDao(),
            db.soilProfileDao(),
            db.soilComponentDao()
        )
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
    fun test_room_database_insert_and_query_plants() {
        runBlocking {
            db.plantDao().insertAll(InitialPlantData.initialPlants)
            val plants = db.plantDao().getAllPlants().first()
            assertTrue(plants.size >= 14)

            val mint = plants.firstOrNull { it.name_ar.contains("نعناع") }
            assertNotNull(mint)
            assertEquals("Spearmint", mint?.name_en)
        }
    }

    @Test
    fun test_v0_3_domain_foundation_hierarchy_garden_place_container_userplant_soilprofile() {
        runBlocking {
            db.plantDao().insertAll(InitialPlantData.initialPlants)
        val plantId = 1

        // 1. Garden
        val gardenId = db.gardenDao().insertGarden(Garden(name = "حديقة السطح"))
        assertEquals(1, gardenId.toInt())

        // 2. Place belongs to Garden
        val balconyPlaceId = db.placeDao().insertPlace(
            Place(garden_id = gardenId.toInt(), name = "البلكونة الشرقية")
        )

        val places = db.placeDao().getPlacesByGardenId(gardenId.toInt()).first()
        assertEquals(1, places.size)
        assertEquals(gardenId.toInt(), places[0].garden_id)

        // 3. Container belongs to Place with GrowingMethod
        val containerId = db.containerDao().insertContainer(
            PlantContainer(
                place_id = balconyPlaceId.toInt(),
                name = "أصيص فخاري كبير",
                type = ContainerType.POT,
                growing_method = GrowingMethod.SOIL,
                material = "فخار طبيعي"
            )
        )

        val containers = db.containerDao().getContainersByPlaceId(balconyPlaceId.toInt()).first()
        assertEquals(1, containers.size)
        assertEquals(GrowingMethod.SOIL, containers[0].growing_method)

        // 4. SoilProfile belongs to Container with DataSource metadata
        val profileId = db.soilProfileDao().insertSoilProfile(
            SoilProfile(
                container_id = containerId.toInt(),
                type = "تربة بيتموس مع كمبوست",
                moisture = 45f,
                pH = 6.5f,
                data_source = DataSource.USER_ENTERED
            )
        )
        val profile = db.soilProfileDao().getSoilProfileByContainerId(containerId.toInt()).first()
        assertNotNull(profile)
        assertEquals(6.5f, profile?.pH)
        assertEquals(DataSource.USER_ENTERED, profile?.data_source)

        // 5. UserPlant belongs to Container
        val userPlant = UserPlant(
            id = 1,
            plant_id = plantId,
            nickname = "نعناع البلكونة",
            container_id = containerId.toInt(),
            added_date = System.currentTimeMillis()
        )
        db.userPlantDao().insertUserPlant(userPlant)

        val userPlants = db.userPlantDao().getAllUserPlants().first()
        assertEquals(1, userPlants.size)
        assertEquals("نعناع البلكونة", userPlants[0].nickname)
        assertEquals(containerId.toInt(), userPlants[0].container_id)

        // 6. CareLog preservation
        val log = CareLog(
            user_plant_id = userPlant.id,
            action_type = "watered",
            timestamp = System.currentTimeMillis()
        )
        db.careLogDao().insertCareLog(log)

        val logs = db.careLogDao().getCareLogsForPlant(userPlant.id).first()
        assertEquals(1, logs.size)
        assertEquals("watered", logs[0].action_type)

        // 7. Repository joins properly reflect Garden, Place, Container, and SoilProfile
        val detailedPlants = repository.getUserPlantsWithDetails().first()
        assertEquals(1, detailedPlants.size)
        val details = detailedPlants[0]
        assertEquals("نعناع البلكونة", details.userPlant.nickname)
        assertEquals("البلكونة الشرقية", details.placeName)
        assertEquals("أصيص فخاري كبير", details.containerName)
        assertEquals(GrowingMethod.SOIL, details.container?.growing_method)
        assertEquals(6.5f, details.soilProfile?.pH)
        }
    }
}
