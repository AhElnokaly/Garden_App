package com.example.data

import com.example.data.dao.CareLogDao
import com.example.data.dao.ContainerDao
import com.example.data.dao.GardenDao
import com.example.data.dao.PlaceDao
import com.example.data.dao.PlantDao
import com.example.data.dao.SoilComponentDao
import com.example.data.dao.SoilProfileDao
import com.example.data.dao.UserPlantDao
import com.example.data.model.CareLog
import com.example.data.model.ContainerType
import com.example.data.model.Garden
import com.example.data.model.GrowingMethod
import com.example.data.model.Place
import com.example.data.model.Plant
import com.example.data.model.PlantContainer
import com.example.data.model.SoilComponent
import com.example.data.model.SoilProfile
import com.example.data.model.UserPlant
import com.example.data.model.UserPlantWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GardenRepository(
    private val plantDao: PlantDao,
    private val gardenDao: GardenDao,
    private val placeDao: PlaceDao,
    private val containerDao: ContainerDao,
    private val userPlantDao: UserPlantDao,
    private val careLogDao: CareLogDao,
    private val soilProfileDao: SoilProfileDao,
    private val soilComponentDao: SoilComponentDao
) {
    suspend fun ensurePlantsSeeded() {
        val count = plantDao.countPlants()
        if (count == 0) {
            plantDao.insertAll(InitialPlantData.initialPlants)
        }
        if (gardenDao.countGardens() == 0) {
            gardenDao.insertGarden(Garden(id = 1, name = "حديقتي الرئيسية"))
        }
        if (placeDao.countPlaces() == 0) {
            placeDao.insertPlace(Place(id = 1, garden_id = 1, name = "البلكونة"))
        }
        if (containerDao.countContainers() == 0) {
            containerDao.insertContainer(
                PlantContainer(
                    id = 1,
                    place_id = 1,
                    name = "أصيص البلكونة 1",
                    type = ContainerType.POT,
                    growing_method = GrowingMethod.SOIL
                )
            )
        }
    }

    // Garden operations
    fun getDefaultGarden(): Flow<Garden?> = gardenDao.getDefaultGarden()

    suspend fun getDefaultGardenSync(): Garden? = gardenDao.getDefaultGardenSync()

    fun getAllGardens(): Flow<List<Garden>> = gardenDao.getAllGardens()

    suspend fun createGarden(name: String): Long {
        return gardenDao.insertGarden(Garden(name = name.trim()))
    }

    // Places operations
    fun getAllPlaces(): Flow<List<Place>> = placeDao.getAllPlaces()

    suspend fun getAllPlacesList(): List<Place> = placeDao.getAllPlacesList()

    suspend fun getOrCreatePlace(name: String, gardenId: Int = 1): Place {
        val trimmed = name.trim()
        val existing = placeDao.getPlaceByName(trimmed)
        if (existing != null) return existing
        val newId = placeDao.insertPlace(Place(garden_id = gardenId, name = trimmed))
        return Place(id = newId.toInt(), garden_id = gardenId, name = trimmed)
    }

    // Containers operations
    fun getAllContainers(): Flow<List<PlantContainer>> = containerDao.getAllContainers()

    fun getContainersByPlaceId(placeId: Int): Flow<List<PlantContainer>> = containerDao.getContainersByPlaceId(placeId)

    suspend fun getOrCreateDefaultContainerForPlace(
        placeId: Int,
        placeName: String = "مكان زراعي",
        growingMethod: GrowingMethod = GrowingMethod.SOIL
    ): PlantContainer {
        val existingList = containerDao.getContainersListByPlaceId(placeId)
        if (existingList.isNotEmpty()) {
            return existingList.first()
        }
        val newContainer = PlantContainer(
            place_id = placeId,
            name = "$placeName - أصيص افتراضي",
            type = ContainerType.POT,
            growing_method = growingMethod
        )
        val newId = containerDao.insertContainer(newContainer)
        return newContainer.copy(id = newId.toInt())
    }

    suspend fun addContainer(
        placeId: Int,
        name: String,
        type: ContainerType = ContainerType.POT,
        growingMethod: GrowingMethod = GrowingMethod.SOIL,
        material: String? = null
    ): Long {
        val container = PlantContainer(
            place_id = placeId,
            name = name.trim(),
            type = type,
            growing_method = growingMethod,
            material = material
        )
        return containerDao.insertContainer(container)
    }

    // Soil Profile operations
    fun getSoilProfileForContainer(containerId: Int): Flow<SoilProfile?> =
        soilProfileDao.getSoilProfileByContainerId(containerId)

    suspend fun saveSoilProfile(soilProfile: SoilProfile): Long {
        return soilProfileDao.insertSoilProfile(soilProfile)
    }

    fun getSoilComponents(soilProfileId: Int): Flow<List<SoilComponent>> =
        soilComponentDao.getComponentsForProfile(soilProfileId)

    suspend fun addSoilComponent(component: SoilComponent): Long =
        soilComponentDao.insertComponent(component)

    // Plants catalog operations
    fun getAllPlants(): Flow<List<Plant>> = plantDao.getAllPlants()

    fun searchPlants(query: String): Flow<List<Plant>> = plantDao.searchPlants(query)

    fun getPlantById(id: Int): Flow<Plant?> = plantDao.getPlantById(id)

    /**
     * Emits the user's plants joined with:
     * Plant info, Container, Place (via Container), SoilProfile, and Care logs.
     */
    fun getUserPlantsWithDetails(): Flow<List<UserPlantWithDetails>> {
        val baseFlow = combine(
            userPlantDao.getAllUserPlants(),
            plantDao.getAllPlants(),
            containerDao.getAllContainers(),
            placeDao.getAllPlaces(),
            careLogDao.getAllCareLogs()
        ) { userPlants, plants, containers, places, allCareLogs ->
            val plantsMap = plants.associateBy { it.id }
            val containersMap = containers.associateBy { it.id }
            val placesMap = places.associateBy { it.id }
            val logsByUserPlant = allCareLogs.groupBy { it.user_plant_id }

            userPlants.mapNotNull { up ->
                val basePlant = plantsMap[up.plant_id] ?: return@mapNotNull null
                val container = containersMap[up.container_id]
                val place = container?.let { placesMap[it.place_id] }
                val plantLogs = logsByUserPlant[up.id].orEmpty()
                val latestLog = plantLogs.firstOrNull()
                val lastWatered = plantLogs.firstOrNull { it.action_type == "watered" }

                UserPlantWithDetails(
                    userPlant = up,
                    plant = basePlant,
                    container = container,
                    place = place,
                    soilProfile = null,
                    lastWateredLog = lastWatered,
                    latestCareLog = latestLog
                )
            }
        }

        return baseFlow.combine(soilProfileDao.getAllSoilProfiles()) { plantDetailsList, soilProfiles ->
            val profilesByContainer = soilProfiles.associateBy { it.container_id }
            plantDetailsList.map { details ->
                val profile = profilesByContainer[details.userPlant.container_id]
                details.copy(soilProfile = profile)
            }
        }
    }

    fun getUserPlantDetails(userPlantId: Int): Flow<UserPlantWithDetails?> {
        val baseFlow = combine(
            userPlantDao.getUserPlantById(userPlantId),
            plantDao.getAllPlants(),
            containerDao.getAllContainers(),
            placeDao.getAllPlaces(),
            careLogDao.getCareLogsForPlant(userPlantId)
        ) { userPlant, plants, containers, places, logs ->
            if (userPlant == null) return@combine null
            val basePlant = plants.firstOrNull { it.id == userPlant.plant_id } ?: return@combine null
            val container = containers.firstOrNull { it.id == userPlant.container_id }
            val place = container?.let { c -> places.firstOrNull { it.id == c.place_id } }
            val latestLog = logs.firstOrNull()
            val lastWatered = logs.firstOrNull { it.action_type == "watered" }

            UserPlantWithDetails(
                userPlant = userPlant,
                plant = basePlant,
                container = container,
                place = place,
                soilProfile = null,
                lastWateredLog = lastWatered,
                latestCareLog = latestLog
            )
        }

        return baseFlow.combine(soilProfileDao.getAllSoilProfiles()) { details, soilProfiles ->
            if (details == null) return@combine null
            val profile = soilProfiles.firstOrNull { it.container_id == details.userPlant.container_id }
            details.copy(soilProfile = profile)
        }
    }

    fun getCareLogsForPlant(userPlantId: Int): Flow<List<CareLog>> {
        return careLogDao.getCareLogsForPlant(userPlantId)
    }

    suspend fun addUserPlant(
        plantId: Int,
        nickname: String,
        placeId: Int,
        containerName: String? = null,
        growingMethod: GrowingMethod = GrowingMethod.SOIL
    ): Long {
        val place = placeDao.getPlaceByIdSync(placeId)
        val placeName = place?.name ?: "البلكونة"

        val container = if (!containerName.isNullOrBlank()) {
            val newC = PlantContainer(
                place_id = placeId,
                name = containerName.trim(),
                type = ContainerType.POT,
                growing_method = growingMethod
            )
            val cId = containerDao.insertContainer(newC)
            newC.copy(id = cId.toInt())
        } else {
            getOrCreateDefaultContainerForPlace(placeId, placeName, growingMethod)
        }

        val userPlant = UserPlant(
            plant_id = plantId,
            nickname = nickname.trim(),
            container_id = container.id,
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
