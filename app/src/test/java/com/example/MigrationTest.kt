package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.GardenDatabase
import com.example.data.GardenDatabase.Companion.MIGRATION_2_3
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MigrationTest {

    @Test
    fun test_migration_from_version_2_to_3_preserves_all_data_and_builds_domain_hierarchy() {
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration_test_v2_to_v3.db"
        context.deleteDatabase(dbName)

        // 1. Create a version 2 database with legacy schema
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(2) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `plants` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `name_ar` TEXT NOT NULL,
                            `name_en` TEXT NOT NULL,
                            `category` TEXT NOT NULL,
                            `water_frequency_days` INTEGER NOT NULL,
                            `light_needs` TEXT NOT NULL,
                            `icon_ref` TEXT NOT NULL
                        )
                        """.trimIndent()
                    )
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `places` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `name` TEXT NOT NULL,
                            `created_date` INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `user_plants` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `plant_id` INTEGER NOT NULL,
                            `nickname` TEXT NOT NULL,
                            `place_id` INTEGER NOT NULL,
                            `added_date` INTEGER NOT NULL,
                            FOREIGN KEY(`plant_id`) REFERENCES `plants`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                            FOREIGN KEY(`place_id`) REFERENCES `places`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT
                        )
                        """.trimIndent()
                    )
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `care_logs` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `user_plant_id` INTEGER NOT NULL,
                            `action_type` TEXT NOT NULL,
                            `timestamp` INTEGER NOT NULL,
                            `note_text` TEXT,
                            `photo_uri` TEXT,
                            FOREIGN KEY(`user_plant_id`) REFERENCES `user_plants`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )

                    // Insert legacy data
                    val now = System.currentTimeMillis()
                    db.execSQL("INSERT INTO `plants` (`id`, `name_ar`, `name_en`, `category`, `water_frequency_days`, `light_needs`, `icon_ref`) VALUES (1, 'نعناع', 'Spearmint', 'herb', 3, 'شمس جزئية', 'mint')")
                    db.execSQL("INSERT INTO `places` (`id`, `name`, `created_date`) VALUES (1, 'البلكونة', $now)")
                    db.execSQL("INSERT INTO `places` (`id`, `name`, `created_date`) VALUES (2, 'السطح', $now)")
                    db.execSQL("INSERT INTO `user_plants` (`id`, `plant_id`, `nickname`, `place_id`, `added_date`) VALUES (10, 1, 'نعناع شرفتي', 1, $now)")
                    db.execSQL("INSERT INTO `care_logs` (`id`, `user_plant_id`, `action_type`, `timestamp`, `note_text`) VALUES (100, 10, 'watered', $now, 'سقيتها ماء معتدل')")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()

        val helper = FrameworkSQLiteOpenHelperFactory().create(config)
        val v2Db = helper.writableDatabase
        v2Db.close()

        // 2. Open with Room using MIGRATION_2_3
        val upgradedDb = Room.databaseBuilder(context, GardenDatabase::class.java, dbName)
            .addMigrations(MIGRATION_2_3)
            .build()

        // 3. Verify that Garden exists
        val gardens = upgradedDb.gardenDao().getAllGardens().first()
        assertTrue(gardens.isNotEmpty())
        assertEquals(1, gardens[0].id)
        assertEquals("حديقتي الرئيسية", gardens[0].name)

        // 4. Verify Place exists and now has garden_id = 1
        val places = upgradedDb.placeDao().getAllPlaces().first()
        assertEquals(2, places.size)
        val balcony = places.firstOrNull { it.id == 1 }
        assertNotNull(balcony)
        assertEquals("البلكونة", balcony?.name)
        assertEquals(1, balcony?.garden_id)

        // 5. Verify Container was created for the legacy plant
        val containers = upgradedDb.containerDao().getAllContainers().first()
        assertTrue(containers.isNotEmpty())
        val plantContainer = containers.firstOrNull { it.id == 10 }
        assertNotNull(plantContainer)
        assertEquals(1, plantContainer?.place_id) // Points to place_id 1
        assertTrue(plantContainer?.name?.contains("نعناع شرفتي") == true)

        // 6. Verify Plant still exists and now points to container_id = 10
        val userPlants = upgradedDb.userPlantDao().getAllUserPlants().first()
        assertEquals(1, userPlants.size)
        val migratedPlant = userPlants[0]
        assertEquals(10, migratedPlant.id)
        assertEquals("نعناع شرفتي", migratedPlant.nickname)
        assertEquals(10, migratedPlant.container_id)

        // 7. Verify CareLog still exists
        val logs = upgradedDb.careLogDao().getCareLogsForPlant(10).first()
        assertEquals(1, logs.size)
        assertEquals("watered", logs[0].action_type)
        assertEquals("سقيتها ماء معتدل", logs[0].note_text)

        upgradedDb.close()
        context.deleteDatabase(dbName)
        }
    }
}
