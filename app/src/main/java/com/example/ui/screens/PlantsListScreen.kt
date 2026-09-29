package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.EmptyPlantsState
import com.example.ui.components.GardenHeroBanner
import com.example.ui.components.GardenWeatherCard
import com.example.ui.components.TodayCareTask
import com.example.ui.components.TodayCareTaskCard
import com.example.ui.components.TodayTaskType
import com.example.ui.components.UpdateDialog
import com.example.ui.components.UserPlantCard
import com.example.ui.theme.GardenBackground
import com.example.ui.theme.GardenTextMuted
import com.example.ui.theme.GardenTextPrimary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.PillShape
import com.example.ui.theme.SkyBlueWater
import com.example.ui.viewmodel.GardenViewModel
import com.example.ui.viewmodel.PlantFilter
import com.example.updater.UpdateState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsListScreen(
    viewModel: GardenViewModel,
    onNavigateToAddPlant: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedPlants by viewModel.displayedUserPlants.collectAsStateWithLifecycle()
    val allPlants by viewModel.allUserPlants.collectAsStateWithLifecycle()
    val allPlaces by viewModel.allPlaces.collectAsStateWithLifecycle()
    val selectedPlaceId by viewModel.selectedPlaceFilterId.collectAsStateWithLifecycle()
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()
    val snackMessage by viewModel.snackBarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showUpdateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackBarMessage()
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is UpdateState.UpdateAvailable || updateState is UpdateState.UpToDate || updateState is UpdateState.Error) {
            showUpdateDialog = true
        }
    }

    // Dynamic tasks for "Today your garden" matching mockup
    val thirstyPlant = allPlants.firstOrNull { it.needsWatering }
    val todayTasks = remember(allPlants) {
        listOf(
            TodayCareTask(
                id = "1",
                title = if (thirstyPlant != null) "${thirstyPlant.userPlant.nickname} بحاجة إلى الماء" else "جميع النباتات مرتوية بصحة",
                timeLabel = if (thirstyPlant != null) "خلال ساعتين 💧" else "تم الري بنجاح",
                type = TodayTaskType.WATER
            ),
            TodayCareTask(
                id = "2",
                title = "النباتات تستقبل ضوء الصباح المثالي",
                timeLabel = "الآن ☀️",
                type = TodayTaskType.SUNLIGHT
            ),
            TodayCareTask(
                id = "3",
                title = "${allPlants.size} نباتات تنمو بنشاط في حديقتك",
                timeLabel = "مرحلة نشطة 🌱",
                type = TodayTaskType.GROWTH
            )
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("plants_list_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = GardenBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("plants_lazy_column"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Greeting (Good morning, Ahmed 🌱 - Your garden is doing well today)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "صباح الخير، أحمد 🌱",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = GardenTextPrimary,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "حديقتك في حالة ممتازة وتنمو بصحة اليوم.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GardenTextMuted,
                            fontSize = 13.sp
                        )
                    }

                    // Update Checker Icon button
                    IconButton(
                        onClick = { viewModel.checkForUpdates() },
                        modifier = Modifier.testTag("check_update_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(GreenContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdate,
                                contentDescription = "فحص التحديثات",
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Weather Card (Mockup Inspired)
            item {
                GardenWeatherCard(
                    temperature = "31°C",
                    condition = "مشمس ☀️",
                    conditionNote = "ظروف نمو مثالية لجميع النباتات"
                )
            }

            // Hero Banner: Garden World (Mockup Inspired)
            item {
                GardenHeroBanner(
                    title = "عالم الحديقة 🌱",
                    subtitle = "نصائح وإرشادات العناية ومتابعة النمو اليوم",
                    onClick = onNavigateToAddPlant
                )
            }

            // Section: Today your garden (اليوم في حديقتك)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اليوم في حديقتك",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GardenTextPrimary
                        )
                        Text(
                            text = "${allPlants.size} نباتات",
                            style = MaterialTheme.typography.labelMedium,
                            color = GreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    todayTasks.forEach { task ->
                        TodayCareTaskCard(
                            task = task,
                            onClick = {
                                if (task.type == TodayTaskType.WATER && thirstyPlant != null) {
                                    onNavigateToDetail(thirstyPlant.userPlant.id)
                                }
                            }
                        )
                    }
                }
            }

            // Section: Care & Place Filters
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "نباتاتي 🌿",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GardenTextPrimary
                    )

                    // Care Status Filter Row: الكل / يحتاج عناية
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = currentFilter == PlantFilter.ALL,
                            onClick = { viewModel.setFilter(PlantFilter.ALL) },
                            label = { Text("الكل (${allPlants.size})") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Yard,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            shape = PillShape,
                            modifier = Modifier.testTag("filter_all")
                        )

                        val needsCareCount = allPlants.count { it.needsWatering }
                        FilterChip(
                            selected = currentFilter == PlantFilter.NEEDS_CARE,
                            onClick = { viewModel.setFilter(PlantFilter.NEEDS_CARE) },
                            label = { Text("يحتاج ري ($needsCareCount)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Opacity,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SkyBlueWater,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            shape = PillShape,
                            modifier = Modifier.testTag("filter_needs_care")
                        )
                    }

                    // Places Filter Row
                    if (allPlaces.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("places_filter_row"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedPlaceId == null,
                                    onClick = { viewModel.setPlaceFilter(null) },
                                    label = { Text("جميع الأماكن") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GreenPrimary,
                                        selectedLabelColor = Color.White,
                                        selectedLeadingIconColor = Color.White
                                    ),
                                    shape = PillShape,
                                    modifier = Modifier.testTag("filter_place_all")
                                )
                            }

                            items(allPlaces, key = { it.id }) { place ->
                                val countInPlace = allPlants.count { it.place?.id == place.id }
                                FilterChip(
                                    selected = selectedPlaceId == place.id,
                                    onClick = { viewModel.setPlaceFilter(place.id) },
                                    label = { Text("${place.name} ($countInPlace)") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GreenPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = PillShape,
                                    modifier = Modifier.testTag("filter_place_${place.id}")
                                )
                            }
                        }
                    }
                }
            }

            // Plants List Items or Empty State
            if (displayedPlants.isEmpty()) {
                item {
                    EmptyPlantsState(
                        currentFilter = currentFilter,
                        onAddPlantClick = onNavigateToAddPlant
                    )
                }
            } else {
                items(displayedPlants, key = { it.userPlant.id }) { plantItem ->
                    UserPlantCard(
                        item = plantItem,
                        onClick = { onNavigateToDetail(plantItem.userPlant.id) },
                        onQuickWater = { viewModel.logWatered(plantItem.userPlant.id) }
                    )
                }
            }
        }

        // Update Dialog
        if (showUpdateDialog) {
            UpdateDialog(
                updateState = updateState,
                onDismiss = {
                    showUpdateDialog = false
                    viewModel.updater.resetState()
                },
                onDownload = { url ->
                    viewModel.downloadAndInstallUpdate(url)
                },
                onCancelDownload = {
                    viewModel.cancelUpdateDownload()
                    showUpdateDialog = false
                },
                onInstall = { apkFile ->
                    viewModel.updater.installApk(apkFile)
                }
            )
        }
    }
}
