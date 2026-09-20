package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CareLogDao
import com.example.data.dao.PlaceDao
import com.example.data.dao.PlantDao
import com.example.data.dao.UserPlantDao
import com.example.data.model.CareLog
import com.example.data.model.Place
import com.example.data.model.Plant
import com.example.data.model.UserPlant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Plant::class,
        Place::class,
        UserPlant::class,
        CareLog::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GardenDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao
    abstract fun placeDao(): PlaceDao
    abstract fun userPlantDao(): UserPlantDao
    abstract fun careLogDao(): CareLogDao

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
                // 1. Create places table
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
                // 2. Ensure default place "البلكونة" exists with id = 1
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO `places` (`id`, `name`, `created_date`) 
                    VALUES (1, 'البلكونة', $currentTime)
                    """.trimIndent()
                )

                // 3. Recreate user_plants with place_id referencing places(id)
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

                // Copy data from old user_plants to user_plants_new
                // Default place_id to 1 (البلكونة)
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

        fun getInstance(context: Context): GardenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GardenDatabase::class.java,
                    "garden_companion.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate with initial plant catalog and default place
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                database.plantDao().insertAll(InitialPlantData.initialPlants)
                                if (database.placeDao().countPlaces() == 0) {
                                    database.placeDao().insertPlace(Place(id = 1, name = "البلكونة"))
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
