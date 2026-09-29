package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SoilComponent
import kotlinx.coroutines.flow.Flow

@Dao
interface SoilComponentDao {
    @Query("SELECT * FROM soil_components WHERE soil_profile_id = :profileId")
    fun getComponentsForProfile(profileId: Int): Flow<List<SoilComponent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponent(component: SoilComponent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComponents(components: List<SoilComponent>)

    @Delete
    suspend fun deleteComponent(component: SoilComponent)

    @Query("DELETE FROM soil_components WHERE soil_profile_id = :profileId")
    suspend fun deleteComponentsForProfile(profileId: Int)
}
