package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CareLogDao
import com.example.data.dao.ContainerDao
import com.example.data.dao.GardenDao
import com.example.data.dao.PlaceDao
import com.example.data.dao.PlantDao
import com.example.data.dao.SoilComponentDao
import com.example.data.dao.SoilProfileDao
import com.example.data.dao.UserPlantDao
import com.example.data.model.CareLog
import com.example.data.model.ContainerType
import com.example.data.model.Garden
import com.example.data.model.GrowingMethod
import com.example.data.model.Place
import com.example.data.model.Plant
import com.example.data.model.PlantContainer
import com.example.data.model.SoilComponent
import com.example.data.model.SoilProfile
import com.example.data.model.UserPlant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Plant::class,
        Garden::class,
        Place::class,
        PlantContainer::class,
        UserPlant::class,
        CareLog::class,
        SoilProfile::class,
        SoilComponent::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(GardenConverters::class)
abstract class GardenDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao
    abstract fun gardenDao(): GardenDao
    abstract fun placeDao(): PlaceDao
    abstract fun containerDao(): ContainerDao
    abstract fun userPlantDao(): UserPlantDao
    abstract fun careLogDao(): CareLogDao
    abstract fun soilProfileDao(): SoilProfileDao
    abstract fun soilComponentDao(): SoilComponentDao

    companion object {
        @Volatile
        private var INSTANCE: GardenDatabase? = null

        /**
         * Migration from version 1 to 2:
         * 1. Create the 'places' table.
         * 2. Insert default place "البلكونة" (id = 1) so existing data is preserved.
         * 3. Migrate 'user_plants' to replace 'place' (TEXT) with 'place_id' (INTEGER FK).
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `places` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `created_date` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                val currentTime = System.currentTimeMillis()
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO `places` (`id`, `name`, `created_date`) 
                    VALUES (1, 'البلكونة', $currentTime)
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_plants_new` (
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
                    INSERT INTO `user_plants_new` (`id`, `plant_id`, `nickname`, `place_id`, `added_date`)
                    SELECT `id`, `plant_id`, `nickname`, 1, `added_date` FROM `user_plants`
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE `user_plants`")
                db.execSQL("ALTER TABLE `user_plants_new` RENAME TO `user_plants`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_plants_plant_id` ON `user_plants` (`plant_id`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_plants_place_id` ON `user_plants` (`place_id`)")
            }
        }

        /**
         * Migration from version 2 to 3 (v0.3 Garden Domain Foundation):
         * 1. Create 'gardens' table and insert default garden 'حديقتي الرئيسية' (id = 1).
         * 2. Migrate 'places' to include 'garden_id' (INTEGER NOT NULL DEFAULT 1) with FK to gardens.
         * 3. Create 'containers' table with place_id FK.
         * 4. Seed default containers for legacy plants and places.
         * 5. Migrate 'user_plants' to reference 'container_id' instead of direct 'place_id'.
         * 6. Create 'soil_profiles' and 'soil_components' tables.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val currentTime = System.currentTimeMillis()

                // 1. Create gardens table and seed default garden
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `gardens` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `created_at` INTEGER NOT NULL,
                        `updated_at` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO `gardens` (`id`, `name`, `created_at`, `updated_at`)
                    VALUES (1, 'حديقتي الرئيسية', $currentTime, $currentTime)
                    """.trimIndent()
                )

                // 2. Recreate places table with garden_id FK
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `places_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `garden_id` INTEGER NOT NULL DEFAULT 1,
                        `name` TEXT NOT NULL,
                        `created_date` INTEGER NOT NULL,
                        FOREIGN KEY(`garden_id`) REFERENCES `gardens`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `places_new` (`id`, `garden_id`, `name`, `created_date`)
                    SELECT `id`, 1, `name`, `created_date` FROM `places`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `places`")
                db.execSQL("ALTER TABLE `places_new` RENAME TO `places`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_places_garden_id` ON `places` (`garden_id`)")

                // 3. Create containers table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `containers` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `place_id` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `material` TEXT,
                        `width` REAL,
                        `length` REAL,
                        `height` REAL,
                        `volume` REAL,
                        `drainage` INTEGER,
                        `growing_method` TEXT NOT NULL,
                        `created_at` INTEGER NOT NULL,
                        `updated_at` INTEGER NOT NULL,
                        FOREIGN KEY(`place_id`) REFERENCES `places`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_containers_place_id` ON `containers` (`place_id`)")

                // 4. Create containers for legacy plants (1-to-1 deterministic mapping)
                db.execSQL(
                    """
                    INSERT INTO `containers` (`id`, `place_id`, `name`, `type`, `material`, `width`, `length`, `height`, `volume`, `drainage`, `growing_method`, `created_at`, `updated_at`)
                    SELECT `id`, `place_id`, `nickname` || ' (أصيص)', 'POT', NULL, NULL, NULL, NULL, NULL, 1, 'SOIL', `added_date`, `added_date`
                    FROM `user_plants`
                    """.trimIndent()
                )

                // For any places that had 0 plants, ensure a default container exists
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO `containers` (`place_id`, `name`, `type`, `material`, `growing_method`, `created_at`, `updated_at`)
                    SELECT `id`, `name` || ' - أصيص عام', 'POT', 'فخار', 'SOIL', `created_date`, `created_date`
                    FROM `places`
                    WHERE `id` NOT IN (SELECT DISTINCT `place_id` FROM `containers`)
                    """.trimIndent()
                )

                // 5. Recreate user_plants with container_id FK referencing containers(id)
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_plants_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `plant_id` INTEGER NOT NULL,
                        `nickname` TEXT NOT NULL,
                        `container_id` INTEGER NOT NULL,
                        `added_date` INTEGER NOT NULL,
                        FOREIGN KEY(`plant_id`) REFERENCES `plants`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`container_id`) REFERENCES `containers`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )
                // Copy user_plants data: container_id matches user_plant.id
                db.execSQL(
                    """
                    INSERT INTO `user_plants_new` (`id`, `plant_id`, `nickname`, `container_id`, `added_date`)
                    SELECT `id`, `plant_id`, `nickname`, `id`, `added_date` FROM `user_plants`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `user_plants`")
                db.execSQL("ALTER TABLE `user_plants_new` RENAME TO `user_plants`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_plants_plant_id` ON `user_plants` (`plant_id`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_plants_container_id` ON `user_plants` (`container_id`)")

                // 6. Create soil_profiles table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `soil_profiles` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `container_id` INTEGER NOT NULL,
                        `type` TEXT,
                        `moisture` REAL,
                        `pH` REAL,
                        `ec` REAL,
                        `soil_temperature` REAL,
                        `data_source` TEXT NOT NULL,
                        `last_updated` INTEGER NOT NULL,
                        FOREIGN KEY(`container_id`) REFERENCES `containers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_soil_profiles_container_id` ON `soil_profiles` (`container_id`)")

                // 7. Create soil_components table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `soil_components` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `soil_profile_id` INTEGER NOT NULL,
                        `component_name` TEXT NOT NULL,
                        `percentage` REAL,
                        `notes` TEXT,
                        FOREIGN KEY(`soil_profile_id`) REFERENCES `soil_profiles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_soil_components_soil_profile_id` ON `soil_components` (`soil_profile_id`)")

                // 8. Ensure care_logs index exists
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_care_logs_user_plant_id` ON `care_logs` (`user_plant_id`)")
            }
        }

        fun getInstance(context: Context): GardenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GardenDatabase::class.java,
                    "garden_companion.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                database.plantDao().insertAll(InitialPlantData.initialPlants)
                                if (database.gardenDao().countGardens() == 0) {
                                    database.gardenDao().insertGarden(Garden(id = 1, name = "حديقتي الرئيسية"))
                                }
                                if (database.placeDao().countPlaces() == 0) {
                                    database.placeDao().insertPlace(Place(id = 1, garden_id = 1, name = "البلكونة"))
                                }
                                if (database.containerDao().countContainers() == 0) {
                                    database.containerDao().insertContainer(
                                        PlantContainer(
                                            id = 1,
                                            place_id = 1,
                                            name = "أصيص البلكونة 1",
                                            type = ContainerType.POT,
                                            growing_method = GrowingMethod.SOIL
                                        )
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
