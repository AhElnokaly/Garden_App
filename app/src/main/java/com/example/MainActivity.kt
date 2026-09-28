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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.GardenBottomBar
import com.example.ui.components.GardenNavTab
import com.example.ui.screens.AddPlantScreen
import com.example.ui.screens.PlantDetailScreen
import com.example.ui.screens.PlantsListScreen
import com.example.ui.screens.PlacesScreen
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTab = when (currentRoute) {
        "plants_list" -> GardenNavTab.HOME
        "places" -> GardenNavTab.PLACES
        else -> GardenNavTab.HOME
    }

    val showBottomBar = currentRoute in listOf("plants_list", "places")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                GardenBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        when (tab) {
                            GardenNavTab.HOME -> {
                                if (currentRoute != "plants_list") {
                                    navController.navigate("plants_list") {
                                        popUpTo("plants_list") { inclusive = true }
                                    }
                                }
                            }
                            GardenNavTab.PLACES -> {
                                if (currentRoute != "places") {
                                    navController.navigate("places")
                                }
                            }
                            GardenNavTab.CATALOG -> {
                                navController.navigate("add_plant")
                            }
                            GardenNavTab.MORE -> {
                                viewModel.checkForUpdates()
                            }
                        }
                    },
                    onAddPlantClick = {
                        navController.navigate("add_plant")
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "plants_list",
            modifier = Modifier.padding(innerPadding)
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

            composable("places") {
                PlacesScreen(
                    viewModel = viewModel,
                    onPlaceSelected = { placeId ->
                        viewModel.setPlaceFilter(placeId)
                        navController.navigate("plants_list")
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
}
