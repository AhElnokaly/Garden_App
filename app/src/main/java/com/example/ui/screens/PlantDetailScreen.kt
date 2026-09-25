package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddCareNoteDialog
import com.example.ui.components.CareLogItemCard
import com.example.ui.components.DeletePlantConfirmDialog
import com.example.ui.components.PlantHeroCard
import com.example.ui.components.QuickCareActionsGrid
import com.example.ui.theme.GreenPrimary
import com.example.ui.viewmodel.GardenViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    userPlantId: Int,
    viewModel: GardenViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(userPlantId) {
        viewModel.selectUserPlant(userPlantId)
    }

    val plantDetails by viewModel.selectedPlantDetails.collectAsStateWithLifecycle()
    val careLogs by viewModel.selectedPlantCareLogs.collectAsStateWithLifecycle()
    val snackMessage by viewModel.snackBarMessage.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }

    var currentPhotoFile by remember { mutableStateOf<File?>(null) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val file = currentPhotoFile
        val uri = currentPhotoUri
        if (success && uri != null) {
            plantDetails?.userPlant?.id?.let { id ->
                viewModel.logPhoto(id, photoUri = uri.toString())
            }
        } else {
            // Cancelled or failed -> delete empty file to prevent orphan files
            if (file != null && file.exists()) {
                file.delete()
            }
        }
        currentPhotoFile = null
        currentPhotoUri = null
    }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackBarMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("plant_detail_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = plantDetails?.userPlant?.nickname ?: "تفاصيل النبتة",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.testTag("delete_plant_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف النبتة",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (plantDetails == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            val item = plantDetails!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("detail_lazy_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Plant Header Card
                item {
                    PlantHeroCard(item = item)
                }

                // Quick Care Action Buttons (4 Buttons as specified)
                item {
                    Text(
                        text = "تسجيل رعاية سريعة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickCareActionsGrid(
                        onWater = { viewModel.logWatered(item.userPlant.id) },
                        onFertilize = { viewModel.logFertilized(item.userPlant.id) },
                        onPhoto = {
                            val photoFile = File(
                                context.filesDir,
                                "plant_photo_${item.userPlant.id}_${System.currentTimeMillis()}.jpg"
                            )
                            val authority = "${context.packageName}.fileprovider"
                            val photoUri = FileProvider.getUriForFile(context, authority, photoFile)
                            currentPhotoFile = photoFile
                            currentPhotoUri = photoUri
                            takePictureLauncher.launch(photoUri)
                        },
                        onNote = { showAddNoteDialog = true }
                    )
                }

                // Care Logs History Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "سجل العناية والتاريخ (${careLogs.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (careLogs.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "🌱", fontSize = 32.sp)
                                Text(
                                    text = "لا توجد سجلات عناية بعد",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "اضغط على أزرار الرعاية السريعة أعلاه لتدوين السقاية أو التسميد أو الصور.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(careLogs, key = { it.id }) { log ->
                        CareLogItemCard(log = log)
                    }
                }
            }
        }

        // Delete Confirm Dialog
        if (showDeleteConfirmDialog) {
            DeletePlantConfirmDialog(
                plantNickname = plantDetails?.userPlant?.nickname ?: "",
                onConfirm = {
                    showDeleteConfirmDialog = false
                    plantDetails?.userPlant?.id?.let { id ->
                        viewModel.deleteUserPlant(id) {
                            onNavigateBack()
                        }
                    }
                },
                onDismiss = { showDeleteConfirmDialog = false }
            )
        }

        // Add Note Dialog
        if (showAddNoteDialog) {
            AddCareNoteDialog(
                onSaveNote = { note ->
                    plantDetails?.userPlant?.id?.let { id ->
                        viewModel.logNote(id, note)
                    }
                    showAddNoteDialog = false
                },
                onDismiss = { showAddNoteDialog = false }
            )
        }
    }
}
