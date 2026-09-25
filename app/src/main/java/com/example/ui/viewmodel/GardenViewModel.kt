package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GardenDatabase
import com.example.data.GardenRepository
import com.example.data.model.CareLog
import com.example.data.model.Place
import com.example.data.model.Plant
import com.example.data.model.UserPlantWithDetails
import com.example.notifications.NotificationScheduler
import com.example.updater.GitHubAppUpdater
import com.example.updater.UpdateState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PlantFilter {
    ALL,
    NEEDS_CARE
}

@OptIn(ExperimentalCoroutinesApi::class)
class GardenViewModel(application: Application) : AndroidViewModel(application) {

    private val database = GardenDatabase.getInstance(application)
    val repository = GardenRepository(
        database.plantDao(),
        database.placeDao(),
        database.userPlantDao(),
        database.careLogDao()
    )

    val updater = GitHubAppUpdater(application)
    val updateState: StateFlow<UpdateState> = updater.updateState

    // Filter for Plants List (All vs Needs Care)
    private val _currentFilter = MutableStateFlow(PlantFilter.ALL)
    val currentFilter: StateFlow<PlantFilter> = _currentFilter.asStateFlow()

    // Place filter: null means All places, otherwise specific Place ID
    private val _selectedPlaceFilterId = MutableStateFlow<Int?>(null)
    val selectedPlaceFilterId: StateFlow<Int?> = _selectedPlaceFilterId.asStateFlow()

    // List of all places
    val allPlaces: StateFlow<List<Place>> = repository.getAllPlaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Base flow of all user plants with details
    val allUserPlants: StateFlow<List<UserPlantWithDetails>> = repository.getUserPlantsWithDetails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered user plants list according to care status and selected place
    val displayedUserPlants: StateFlow<List<UserPlantWithDetails>> = combine(
        allUserPlants,
        _currentFilter,
        _selectedPlaceFilterId
    ) { plants, filter, placeId ->
        val careFiltered = when (filter) {
            PlantFilter.ALL -> plants
            PlantFilter.NEEDS_CARE -> plants.filter { it.needsWatering }
        }
        if (placeId == null) {
            careFiltered
        } else {
            careFiltered.filter { it.userPlant.place_id == placeId }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Catalog search & category filter for Add Plant screen
    val catalogSearchQuery = MutableStateFlow("")
    val catalogCategoryFilter = MutableStateFlow("ALL") // ALL, herb, vegetable, ornamental

    val availablePlants: StateFlow<List<Plant>> = combine(
        repository.getAllPlants(),
        catalogSearchQuery,
        catalogCategoryFilter
    ) { plants, query, category ->
        plants.filter { plant ->
            val matchesQuery = query.isBlank() ||
                plant.name_ar.contains(query, ignoreCase = true) ||
                plant.name_en.contains(query, ignoreCase = true)
            val matchesCategory = category == "ALL" || plant.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active plant detail tracking
    private val _selectedUserPlantId = MutableStateFlow<Int?>(null)
    val selectedUserPlantId: StateFlow<Int?> = _selectedUserPlantId.asStateFlow()

    val selectedPlantDetails: StateFlow<UserPlantWithDetails?> = _selectedUserPlantId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(null)
        else repository.getUserPlantDetails(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedPlantCareLogs: StateFlow<List<CareLog>> = _selectedUserPlantId.flatMapLatest { id ->
        if (id == null) MutableStateFlow(emptyList())
        else repository.getCareLogsForPlant(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback message (e.g. "تم تسجيل ري النبتة بنجاح 💧")
    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensurePlantsSeeded()
            NotificationScheduler.scheduleDailyWaterCheck(application)
        }
    }

    fun setFilter(filter: PlantFilter) {
        _currentFilter.value = filter
    }

    fun setPlaceFilter(placeId: Int?) {
        _selectedPlaceFilterId.value = placeId
    }

    fun selectUserPlant(id: Int) {
        _selectedUserPlantId.value = id
    }

    fun clearSelectedUserPlant() {
        _selectedUserPlantId.value = null
    }

    fun clearSnackBarMessage() {
        _snackBarMessage.value = null
    }

    fun addPlantToGarden(plantId: Int, nickname: String, placeName: String, onSuccess: () -> Unit) {
        if (nickname.isBlank()) return
        viewModelScope.launch {
            val place = repository.getOrCreatePlace(placeName.ifBlank { "البلكونة" })
            repository.addUserPlant(
                plantId = plantId,
                nickname = nickname.trim(),
                placeId = place.id
            )
            _snackBarMessage.value = "تمت إضافة النبتة إلى حديقتك بنجاح 🌱"
            onSuccess()
        }
    }

    fun deleteUserPlant(id: Int, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUserPlant(id)
            _snackBarMessage.value = "تم حذف الأصيص من الحديقة"
            onDeleted()
        }
    }

    fun logWatered(userPlantId: Int) {
        viewModelScope.launch {
            repository.logCareAction(userPlantId = userPlantId, actionType = "watered")
            _snackBarMessage.value = "💧 تم تسجيل الري بنجاح! النبتة سعيدة الآن"
        }
    }

    fun logFertilized(userPlantId: Int) {
        viewModelScope.launch {
            repository.logCareAction(userPlantId = userPlantId, actionType = "fertilized")
            _snackBarMessage.value = "🧪 تم تسجيل التسميد بنجاح لتغذية النبتة"
        }
    }

    fun logPhoto(userPlantId: Int, photoUri: String? = null) {
        viewModelScope.launch {
            repository.logCareAction(
                userPlantId = userPlantId,
                actionType = "photo",
                photoUri = photoUri ?: "local_photo_${System.currentTimeMillis()}"
            )
            _snackBarMessage.value = "📸 تم التقاط وحفظ صورة النبتة في السجل"
        }
    }

    fun logNote(userPlantId: Int, noteText: String) {
        if (noteText.isBlank()) return
        viewModelScope.launch {
            repository.logCareAction(
                userPlantId = userPlantId,
                actionType = "note",
                noteText = noteText.trim()
            )
            _snackBarMessage.value = "📝 تم حفظ الملاحظة في سجل النبتة"
        }
    }

    private var updateDownloadJob: kotlinx.coroutines.Job? = null

    fun checkForUpdates() {
        viewModelScope.launch {
            updater.checkForUpdates()
        }
    }

    fun downloadAndInstallUpdate(url: String) {
        updateDownloadJob?.cancel()
        updateDownloadJob = viewModelScope.launch {
            updater.downloadAndInstallApk(url)
        }
    }

    fun cancelUpdateDownload() {
        updateDownloadJob?.cancel()
        updateDownloadJob = null
        updater.cancelDownload()
    }
}
