package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CareLog
import kotlinx.coroutines.flow.Flow

@Dao
interface CareLogDao {
    @Query("SELECT * FROM care_logs WHERE user_plant_id = :userPlantId ORDER BY timestamp DESC")
    fun getCareLogsForPlant(userPlantId: Int): Flow<List<CareLog>>

    @Query("SELECT * FROM care_logs WHERE user_plant_id = :userPlantId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestCareLogForPlant(userPlantId: Int): Flow<CareLog?>

    @Query("SELECT * FROM care_logs WHERE user_plant_id = :userPlantId AND action_type = 'watered' ORDER BY timestamp DESC LIMIT 1")
    fun getLatestWateredLogForPlant(userPlantId: Int): Flow<CareLog?>

    @Query("SELECT * FROM care_logs ORDER BY timestamp DESC")
    fun getAllCareLogs(): Flow<List<CareLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareLog(careLog: CareLog): Long

    @Query("DELETE FROM care_logs WHERE id = :id")
    suspend fun deleteCareLog(id: Int)
}
