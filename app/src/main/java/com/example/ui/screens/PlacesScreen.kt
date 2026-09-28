package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PlaceItemCard
import com.example.ui.theme.GardenBackground
import com.example.ui.theme.GardenSurface
import com.example.ui.theme.GardenTextMuted
import com.example.ui.theme.GardenTextPrimary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OnGreenPrimary
import com.example.ui.theme.PillShape
import com.example.ui.viewmodel.GardenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesScreen(
    viewModel: GardenViewModel,
    onPlaceSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val places by viewModel.allPlaces.collectAsStateWithLifecycle()
    val allPlants by viewModel.allUserPlants.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var newPlaceName by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("places_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "أماكني الزراعية 🏡",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(PillShape)
                                .background(GreenContainer)
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${places.size}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GardenBackground
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GreenPrimary,
                contentColor = OnGreenPrimary,
                shape = PillShape,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_place_fab")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة مكان"
                    )
                    Text(
                        text = "إضافة مكان جديد",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = GardenBackground
    ) { paddingValues ->
        if (places.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(GreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "لا توجد أماكن زراعية مضافة بعد",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = GardenTextPrimary
                    )
                    Text(
                        text = "أضف شرفتك، السطح، أو نافذة الغرفة لتنظيم نباتاتك",
                        style = MaterialTheme.typography.bodySmall,
                        color = GardenTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("places_lazy_column"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(places, key = { it.id }) { place ->
                    val plantCount = allPlants.count { it.userPlant.place_id == place.id }
                    PlaceItemCard(
                        place = place,
                        plantCount = plantCount,
                        onClick = { onPlaceSelected(place.id) }
                    )
                }
            }
        }

        // Add Place Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = {
                    Text(
                        text = "إضافة مكان زراعي جديد 🏡",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "اكتب اسم المكان (مثال: شرفة الصباح، السطح، غرفة المعيشة)",
                            style = MaterialTheme.typography.bodySmall,
                            color = GardenTextMuted
                        )
                        OutlinedTextField(
                            value = newPlaceName,
                            onValueChange = { newPlaceName = it },
                            label = { Text("اسم المكان") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("place_name_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPlaceName.isNotBlank()) {
                                viewModel.createPlace(newPlaceName)
                                newPlaceName = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.testTag("confirm_add_place_button")
                    ) {
                        Text("إضافة", color = OnGreenPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}
