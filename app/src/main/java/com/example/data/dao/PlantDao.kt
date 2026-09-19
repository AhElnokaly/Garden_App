package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY name_ar ASC")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE name_ar LIKE '%' || :query || '%' OR name_en LIKE '%' || :query || '%' ORDER BY name_ar ASC")
    fun searchPlants(query: String): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :id LIMIT 1")
    fun getPlantById(id: Int): Flow<Plant?>

    @Query("SELECT * FROM plants WHERE id = :id LIMIT 1")
    suspend fun getPlantByIdSync(id: Int): Plant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Plant>)

    @Query("SELECT COUNT(*) FROM plants")
    suspend fun countPlants(): Int
}
