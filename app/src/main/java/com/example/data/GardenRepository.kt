package com.example.data

import com.example.data.dao.CareLogDao
import com.example.data.dao.PlaceDao
import com.example.data.dao.PlantDao
import com.example.data.dao.UserPlantDao
import com.example.data.model.CareLog
import com.example.data.model.Place
import com.example.data.model.Plant
import com.example.data.model.UserPlant
import com.example.data.model.UserPlantWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

class GardenRepository(
    private val plantDao: PlantDao,
    private val placeDao: PlaceDao,
    private val userPlantDao: UserPlantDao,
    private val careLogDao: CareLogDao
) {
    suspend fun ensurePlantsSeeded() {
        val count = plantDao.countPlants()
        if (count == 0) {
            plantDao.insertAll(InitialPlantData.initialPlants)
        }
        if (placeDao.countPlaces() == 0) {
            placeDao.insertPlace(Place(id = 1, name = "البلكونة"))
        }
    }

    // Places operations
    fun getAllPlaces(): Flow<List<Place>> = placeDao.getAllPlaces()

    suspend fun getAllPlacesList(): List<Place> = placeDao.getAllPlacesList()

    suspend fun getOrCreatePlace(name: String): Place {
        val trimmed = name.trim()
        val existing = placeDao.getPlaceByName(trimmed)
        if (existing != null) return existing
        val newId = placeDao.insertPlace(Place(name = trimmed))
        return Place(id = newId.toInt(), name = trimmed)
    }

    fun getAllPlants(): Flow<List<Plant>> = plantDao.getAllPlants()

    fun searchPlants(query: String): Flow<List<Plant>> = plantDao.searchPlants(query)

    fun getPlantById(id: Int): Flow<Plant?> = plantDao.getPlantById(id)

    /**
     * Emits the user's plants joined with the base Plant info, Place, and their latest care actions.
     */
    fun getUserPlantsWithDetails(): Flow<List<UserPlantWithDetails>> {
        return combine(
            userPlantDao.getAllUserPlants(),
            plantDao.getAllPlants(),
            placeDao.getAllPlaces(),
            careLogDao.getAllCareLogs()
        ) { userPlants, plants, places, allCareLogs ->
            val plantsMap = plants.associateBy { it.id }
            val placesMap = places.associateBy { it.id }
            val logsByUserPlant = allCareLogs.groupBy { it.user_plant_id }

            userPlants.mapNotNull { up ->
                val basePlant = plantsMap[up.plant_id] ?: return@mapNotNull null
                val place = placesMap[up.place_id]
                val plantLogs = logsByUserPlant[up.id].orEmpty()
                val latestLog = plantLogs.firstOrNull()
                val lastWatered = plantLogs.firstOrNull { it.action_type == "watered" }

                UserPlantWithDetails(
                    userPlant = up,
                    plant = basePlant,
                    place = place,
                    lastWateredLog = lastWatered,
                    latestCareLog = latestLog
                )
            }
        }
    }

    fun getUserPlantDetails(userPlantId: Int): Flow<UserPlantWithDetails?> {
        return combine(
            userPlantDao.getUserPlantById(userPlantId),
            plantDao.getAllPlants(),
            placeDao.getAllPlaces(),
            careLogDao.getCareLogsForPlant(userPlantId)
        ) { userPlant, plants, places, logs ->
            if (userPlant == null) return@combine null
            val basePlant = plants.firstOrNull { it.id == userPlant.plant_id } ?: return@combine null
            val place = places.firstOrNull { it.id == userPlant.place_id }
            val latestLog = logs.firstOrNull()
            val lastWatered = logs.firstOrNull { it.action_type == "watered" }

            UserPlantWithDetails(
                userPlant = userPlant,
                plant = basePlant,
                place = place,
                lastWateredLog = lastWatered,
                latestCareLog = latestLog
            )
        }
    }

    fun getCareLogsForPlant(userPlantId: Int): Flow<List<CareLog>> {
        return careLogDao.getCareLogsForPlant(userPlantId)
    }

    suspend fun addUserPlant(plantId: Int, nickname: String, placeId: Int): Long {
        val userPlant = UserPlant(
            plant_id = plantId,
            nickname = nickname.trim(),
            place_id = placeId,
            added_date = System.currentTimeMillis()
        )
        return userPlantDao.insertUserPlant(userPlant)
    }

    suspend fun deleteUserPlant(userPlantId: Int) {
        userPlantDao.deleteUserPlantById(userPlantId)
    }

    suspend fun logCareAction(
        userPlantId: Int,
        actionType: String,
        noteText: String? = null,
        photoUri: String? = null
    ): Long {
        val log = CareLog(
            user_plant_id = userPlantId,
            action_type = actionType,
            timestamp = System.currentTimeMillis(),
            note_text = noteText?.takeIf { it.isNotBlank() },
            photo_uri = photoUri?.takeIf { it.isNotBlank() }
        )
        return careLogDao.insertCareLog(log)
    }
}
