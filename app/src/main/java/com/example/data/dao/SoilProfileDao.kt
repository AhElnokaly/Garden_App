package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SoilProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilProfileDao {
    @Query("SELECT * FROM soil_profiles WHERE container_id = :containerId LIMIT 1")
    fun getSoilProfileByContainerId(containerId: Int): Flow<SoilProfile?>

    @Query("SELECT * FROM soil_profiles WHERE container_id = :containerId LIMIT 1")
    suspend fun getSoilProfileByContainerIdSync(containerId: Int): SoilProfile?

    @Query("SELECT * FROM soil_profiles")
    fun getAllSoilProfiles(): Flow<List<SoilProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoilProfile(soilProfile: SoilProfile): Long

    @Update
    suspend fun updateSoilProfile(soilProfile: SoilProfile)

    @Delete
    suspend fun deleteSoilProfile(soilProfile: SoilProfile)
}
