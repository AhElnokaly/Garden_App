package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BottomBarShape
import com.example.ui.theme.GardenBackground
import com.example.ui.theme.GardenOutline
import com.example.ui.theme.GardenSurface
import com.example.ui.theme.GardenTextMuted
import com.example.ui.theme.GardenTextPrimary
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OnGreenPrimary

enum class GardenNavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
    PLACES("الأماكن", Icons.Filled.LocationOn, Icons.Outlined.LocationOn),
    CATALOG("الموسوعة", Icons.Filled.Eco, Icons.Outlined.Eco),
    MORE("المزيد", Icons.Filled.Menu, Icons.Outlined.Menu)
}

@Composable
fun GardenBottomBar(
    currentTab: GardenNavTab,
    onTabSelected: (GardenNavTab) -> Unit,
    onAddPlantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, shape = BottomBarShape),
        shape = BottomBarShape,
        color = GardenSurface,
        tonalElevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(68.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home
                GardenNavItem(
                    tab = GardenNavTab.HOME,
                    isSelected = currentTab == GardenNavTab.HOME,
                    onClick = { onTabSelected(GardenNavTab.HOME) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 2: Places
                GardenNavItem(
                    tab = GardenNavTab.PLACES,
                    isSelected = currentTab == GardenNavTab.PLACES,
                    onClick = { onTabSelected(GardenNavTab.PLACES) },
                    modifier = Modifier.weight(1f)
                )

                // Center Spacer for elevated (+) button
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Center FAB inside elevated circle
                    Box(
                        modifier = Modifier
                            .offset(y = (-12).dp)
                            .size(54.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(GreenPrimary)
                            .clickable(onClick = onAddPlantClick)
                            .testTag("center_add_plant_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إضافة نبتة جديدة",
                            tint = OnGreenPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Tab 3: Catalog / Knowledge
                GardenNavItem(
                    tab = GardenNavTab.CATALOG,
                    isSelected = currentTab == GardenNavTab.CATALOG,
                    onClick = { onTabSelected(GardenNavTab.CATALOG) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 4: More
                GardenNavItem(
                    tab = GardenNavTab.MORE,
                    isSelected = currentTab == GardenNavTab.MORE,
                    onClick = { onTabSelected(GardenNavTab.MORE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GardenNavItem(
    tab: GardenNavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val tint = if (isSelected) GreenPrimary else GardenTextMuted

    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
            contentDescription = tab.title,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = tab.title,
            color = tint,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
