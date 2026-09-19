package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UserPlant
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPlantDao {
    @Query("SELECT * FROM user_plants ORDER BY added_date DESC")
    fun getAllUserPlants(): Flow<List<UserPlant>>

    @Query("SELECT * FROM user_plants WHERE id = :id LIMIT 1")
    fun getUserPlantById(id: Int): Flow<UserPlant?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPlant(userPlant: UserPlant): Long

    @Delete
    suspend fun deleteUserPlant(userPlant: UserPlant)

    @Query("DELETE FROM user_plants WHERE id = :id")
    suspend fun deleteUserPlantById(id: Int)
}
