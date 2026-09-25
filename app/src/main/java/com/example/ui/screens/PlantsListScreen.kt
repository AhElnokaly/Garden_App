package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.EmptyPlantsState
import com.example.ui.components.UpdateDialog
import com.example.ui.components.UserPlantCard
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.SkyBlueWater
import com.example.ui.viewmodel.GardenViewModel
import com.example.ui.viewmodel.PlantFilter
import com.example.updater.UpdateState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsListScreen(
    viewModel: GardenViewModel,
    onNavigateToAddPlant: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("plants_list_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🌱 رفيق الحديقة",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        Surface(
                            shape = CircleShape,
                            color = GreenContainer
                        ) {
                            Text(
                                text = "${allPlants.size}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.checkForUpdates() },
                        modifier = Modifier.testTag("check_update_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "فحص التحديثات",
                            tint = GreenPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddPlant,
                containerColor = GreenPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_plant_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة نبتة"
                    )
                    Text(
                        text = "إضافة نبتة",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Care Status Filter Row: الكل / يحتاج عناية
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
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
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenPrimary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_all")
                )

                val needsCareCount = allPlants.count { it.needsWatering }
                FilterChip(
                    selected = currentFilter == PlantFilter.NEEDS_CARE,
                    onClick = { viewModel.setFilter(PlantFilter.NEEDS_CARE) },
                    label = { Text("يحتاج عناية ($needsCareCount)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SkyBlueWater,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_needs_care")
                )
            }

            // Places Filter Row: جميع الأماكن / مكان 1 / مكان 2
            if (allPlaces.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
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
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_place_all")
                        )
                    }

                    items(allPlaces, key = { it.id }) { place ->
                        val countInPlace = allPlants.count { it.userPlant.place_id == place.id }
                        FilterChip(
                            selected = selectedPlaceId == place.id,
                            onClick = { viewModel.setPlaceFilter(place.id) },
                            label = { Text("${place.name} ($countInPlace)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_place_${place.id}")
                        )
                    }
                }
            }

            // Plants List or Empty State
            if (displayedPlants.isEmpty()) {
                EmptyPlantsState(
                    currentFilter = currentFilter,
                    onAddPlantClick = onNavigateToAddPlant
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("plants_lazy_column"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedPlants, key = { it.userPlant.id }) { plantItem ->
                        UserPlantCard(
                            item = plantItem,
                            onClick = { onNavigateToDetail(plantItem.userPlant.id) },
                            onQuickWater = { viewModel.logWatered(plantItem.userPlant.id) }
                        )
                    }
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
