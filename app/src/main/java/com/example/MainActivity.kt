package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AddPlantScreen
import com.example.ui.screens.PlantDetailScreen
import com.example.ui.screens.PlantsListScreen
import com.example.ui.theme.GardenCompanionTheme
import com.example.ui.viewmodel.GardenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GardenCompanionTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                ) {
                    NotificationPermissionHandler()
                    GardenAppNavigation()
                }
            }
        }
    }
}

@Composable
fun NotificationPermissionHandler() {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // Permission result handled gracefully
        }

        LaunchedEffect(Unit) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun GardenAppNavigation() {
    val navController = rememberNavController()
    val viewModel: GardenViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "plants_list"
    ) {
        composable("plants_list") {
            PlantsListScreen(
                viewModel = viewModel,
                onNavigateToAddPlant = {
                    navController.navigate("add_plant")
                },
                onNavigateToDetail = { userPlantId ->
                    navController.navigate("plant_detail/$userPlantId")
                }
            )
        }

        composable("add_plant") {
            AddPlantScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "plant_detail/{userPlantId}",
            arguments = listOf(navArgument("userPlantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userPlantId = backStackEntry.arguments?.getInt("userPlantId") ?: 0
            PlantDetailScreen(
                userPlantId = userPlantId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
