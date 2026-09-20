package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.model.Place
import com.example.data.model.Plant
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.SkyBlueWater

@Composable
fun AddPlantNicknameDialog(
    plant: Plant,
    existingPlaces: List<Place>,
    onConfirm: (nickname: String, placeName: String) -> Unit,
    onDismiss: () -> Unit
) {
    var potNickname by remember { mutableStateOf(plant.name_ar) }
    var nicknameError by remember { mutableStateOf(false) }

    // Place selection logic
    val defaultPlaceName = existingPlaces.firstOrNull()?.name ?: "البلكونة"
    var selectedPlaceName by remember { mutableStateOf(defaultPlaceName) }
    var isCreatingNewPlace by remember { mutableStateOf(false) }
    var newPlaceInput by remember { mutableStateOf("") }
    var placeError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlantAvatar(
                    category = plant.category,
                    iconRef = plant.icon_ref,
                    size = 44.dp
                )
                Column {
                    Text(
                        text = "إضافة ${plant.name_ar}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = plant.name_en,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = potNickname,
                    onValueChange = {
                        potNickname = it
                        if (it.isNotBlank()) nicknameError = false
                    },
                    label = { Text("اسم الأصيص أو النبتة المستعار") },
                    placeholder = { Text("مثال: نعناع الشاي، ريحان البلكونة...") },
                    isError = nicknameError,
                    supportingText = {
                        if (nicknameError) Text("يرجى إدخال اسم مستعار للأصيص")
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pot_nickname_input")
                )

                // Place Selection Section
                Text(
                    text = "مكان وضع النبتة 📍",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                // Chips for selecting existing places or adding a new place
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(existingPlaces) { place ->
                        FilterChip(
                            selected = !isCreatingNewPlace && selectedPlaceName == place.name,
                            onClick = {
                                isCreatingNewPlace = false
                                selectedPlaceName = place.name
                                placeError = false
                            },
                            label = { Text(place.name) },
                            leadingIcon = if (!isCreatingNewPlace && selectedPlaceName == place.name) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }

                    item {
                        FilterChip(
                            selected = isCreatingNewPlace,
                            onClick = {
                                isCreatingNewPlace = true
                                placeError = false
                            },
                            label = { Text("➕ مكان جديد") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                if (isCreatingNewPlace) {
                    OutlinedTextField(
                        value = newPlaceInput,
                        onValueChange = {
                            newPlaceInput = it
                            if (it.isNotBlank()) placeError = false
                        },
                        label = { Text("اسم المكان الجديد") },
                        placeholder = { Text("مثال: السطح، الصالة، الشباك...") },
                        isError = placeError,
                        supportingText = {
                            if (placeError) Text("يرجى كتابة اسم المكان")
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_place_input")
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Opacity,
                        contentDescription = null,
                        tint = SkyBlueWater,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "دورة الري: كل ${plant.water_frequency_days} أيام",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (potNickname.isBlank()) {
                        nicknameError = true
                        return@Button
                    }
                    val finalPlaceName = if (isCreatingNewPlace) {
                        if (newPlaceInput.isBlank()) {
                            placeError = true
                            return@Button
                        }
                        newPlaceInput.trim()
                    } else {
                        selectedPlaceName
                    }

                    onConfirm(potNickname, finalPlaceName)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier.testTag("confirm_add_button")
            ) {
                Text("إضافة إلى حديقتي 🌱", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
