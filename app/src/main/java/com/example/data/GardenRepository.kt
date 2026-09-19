package com.example.data

import com.example.data.dao.CareLogDao
import com.example.data.dao.PlantDao
import com.example.data.dao.UserPlantDao
import com.example.data.model.CareLog
import com.example.data.model.Plant
import com.example.data.model.UserPlant
import com.example.data.model.UserPlantWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class GardenRepository(
    private val plantDao: PlantDao,
    private val userPlantDao: UserPlantDao,
    private val careLogDao: CareLogDao
) {
    suspend fun ensurePlantsSeeded() {
        val count = plantDao.countPlants()
        if (count == 0) {
            plantDao.insertAll(InitialPlantData.initialPlants)
        }
    }

    fun getAllPlants(): Flow<List<Plant>> = plantDao.getAllPlants()

    fun searchPlants(query: String): Flow<List<Plant>> = plantDao.searchPlants(query)

    fun getPlantById(id: Int): Flow<Plant?> = plantDao.getPlantById(id)

    /**
     * Emits the user's plants joined with the base Plant info and their latest care actions.
     */
    fun getUserPlantsWithDetails(): Flow<List<UserPlantWithDetails>> {
        return combine(
            userPlantDao.getAllUserPlants(),
            plantDao.getAllPlants(),
            careLogDao.getAllCareLogs()
        ) { userPlants, plants, allCareLogs ->
            val plantsMap = plants.associateBy { it.id }
            val logsByUserPlant = allCareLogs.groupBy { it.user_plant_id }

            userPlants.mapNotNull { up ->
                val basePlant = plantsMap[up.plant_id] ?: return@mapNotNull null
                val plantLogs = logsByUserPlant[up.id].orEmpty()
                val latestLog = plantLogs.firstOrNull()
                val lastWatered = plantLogs.firstOrNull { it.action_type == "watered" }

                UserPlantWithDetails(
                    userPlant = up,
                    plant = basePlant,
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
            careLogDao.getCareLogsForPlant(userPlantId)
        ) { userPlant, plants, logs ->
            if (userPlant == null) return@combine null
            val basePlant = plants.firstOrNull { it.id == userPlant.plant_id } ?: return@combine null
            val latestLog = logs.firstOrNull()
            val lastWatered = logs.firstOrNull { it.action_type == "watered" }

            UserPlantWithDetails(
                userPlant = userPlant,
                plant = basePlant,
                lastWateredLog = lastWatered,
                latestCareLog = latestLog
            )
        }
    }

    fun getCareLogsForPlant(userPlantId: Int): Flow<List<CareLog>> {
        return careLogDao.getCareLogsForPlant(userPlantId)
    }

    suspend fun addUserPlant(plantId: Int, nickname: String, place: String = "البلكونة"): Long {
        val userPlant = UserPlant(
            plant_id = plantId,
            nickname = nickname.trim(),
            place = place,
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
