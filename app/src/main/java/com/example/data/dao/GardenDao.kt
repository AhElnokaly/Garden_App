package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Garden
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenDao {
    @Query("SELECT * FROM gardens WHERE id = :id LIMIT 1")
    fun getGardenById(id: Int): Flow<Garden?>

    @Query("SELECT * FROM gardens ORDER BY id ASC LIMIT 1")
    fun getDefaultGarden(): Flow<Garden?>

    @Query("SELECT * FROM gardens ORDER BY id ASC LIMIT 1")
    suspend fun getDefaultGardenSync(): Garden?

    @Query("SELECT * FROM gardens ORDER BY name ASC")
    fun getAllGardens(): Flow<List<Garden>>

    @Query("SELECT COUNT(*) FROM gardens")
    suspend fun countGardens(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGarden(garden: Garden): Long

    @Update
    suspend fun updateGarden(garden: Garden)
}
