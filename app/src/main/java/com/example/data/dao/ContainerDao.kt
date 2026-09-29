package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PlantContainer
import kotlinx.coroutines.flow.Flow

@Dao
interface ContainerDao {
    @Query("SELECT * FROM containers ORDER BY name ASC")
    fun getAllContainers(): Flow<List<PlantContainer>>

    @Query("SELECT * FROM containers WHERE place_id = :placeId ORDER BY name ASC")
    fun getContainersByPlaceId(placeId: Int): Flow<List<PlantContainer>>

    @Query("SELECT * FROM containers WHERE place_id = :placeId ORDER BY name ASC")
    suspend fun getContainersListByPlaceId(placeId: Int): List<PlantContainer>

    @Query("SELECT * FROM containers WHERE id = :id LIMIT 1")
    fun getContainerById(id: Int): Flow<PlantContainer?>

    @Query("SELECT * FROM containers WHERE id = :id LIMIT 1")
    suspend fun getContainerByIdSync(id: Int): PlantContainer?

    @Query("SELECT COUNT(*) FROM containers")
    suspend fun countContainers(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContainer(container: PlantContainer): Long

    @Update
    suspend fun updateContainer(container: PlantContainer)

    @Delete
    suspend fun deleteContainer(container: PlantContainer)

    @Query("DELETE FROM containers WHERE id = :id")
    suspend fun deleteContainerById(id: Int)
}
