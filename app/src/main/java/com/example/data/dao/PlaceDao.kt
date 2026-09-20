package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getAllPlaces(): Flow<List<Place>>

    @Query("SELECT * FROM places ORDER BY name ASC")
    suspend fun getAllPlacesList(): List<Place>

    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    fun getPlaceById(id: Int): Flow<Place?>

    @Query("SELECT * FROM places WHERE name = :name LIMIT 1")
    suspend fun getPlaceByName(name: String): Place?

    @Query("SELECT COUNT(*) FROM places")
    suspend fun countPlaces(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: Place): Long

    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deletePlaceById(id: Int)
}
