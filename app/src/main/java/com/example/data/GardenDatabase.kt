package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CareLogDao
import com.example.data.dao.PlantDao
import com.example.data.dao.UserPlantDao
import com.example.data.model.CareLog
import com.example.data.model.Plant
import com.example.data.model.UserPlant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Plant::class,
        UserPlant::class,
        CareLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GardenDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao
    abstract fun userPlantDao(): UserPlantDao
    abstract fun careLogDao(): CareLogDao

    companion object {
        @Volatile
        private var INSTANCE: GardenDatabase? = null

        fun getInstance(context: Context): GardenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GardenDatabase::class.java,
                    "garden_companion.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with initial plant catalog
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).plantDao().insertAll(InitialPlantData.initialPlants)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
