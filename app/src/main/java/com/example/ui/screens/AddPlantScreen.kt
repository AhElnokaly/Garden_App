package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Plant
import com.example.ui.components.AddPlantNicknameDialog
import com.example.ui.components.CatalogPlantCard
import com.example.ui.theme.GreenPrimary
import com.example.ui.viewmodel.GardenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    viewModel: GardenViewModel,
    onNavigateBack: () -> Unit
) {
    val availablePlants by viewModel.availablePlants.collectAsStateWithLifecycle()
    val searchQuery by viewModel.catalogSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.catalogCategoryFilter.collectAsStateWithLifecycle()

    var selectedPlantForAdding by remember { mutableStateOf<Plant?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("add_plant_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "إضافة نبتة جديدة",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.catalogSearchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_plant_input"),
                placeholder = { Text("ابحث بالاسم العربي أو الإنجليزي...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.catalogSearchQuery.value = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == "ALL",
                    onClick = { viewModel.catalogCategoryFilter.value = "ALL" },
                    label = { Text("الكل") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedCategory == "herb",
                    onClick = { viewModel.catalogCategoryFilter.value = "herb" },
                    label = { Text("أعشاب") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedCategory == "vegetable",
                    onClick = { viewModel.catalogCategoryFilter.value = "vegetable" },
                    label = { Text("خضار") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedCategory == "ornamental",
                    onClick = { viewModel.catalogCategoryFilter.value = "ornamental" },
                    label = { Text("زينة") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Catalog List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("catalog_lazy_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availablePlants, key = { it.id }) { plant ->
                    CatalogPlantCard(
                        plant = plant,
                        onSelect = { selectedPlantForAdding = plant }
                    )
                }
            }
        }

        // Dialog for specifying the pot nickname and confirming addition
        selectedPlantForAdding?.let { plant ->
            AddPlantNicknameDialog(
                plant = plant,
                onConfirm = { nickname ->
                    viewModel.addPlantToGarden(plant.id, nickname) {
                        selectedPlantForAdding = null
                        onNavigateBack()
                    }
                },
                onDismiss = { selectedPlantForAdding = null }
            )
        }
    }
}
