package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSun
import com.example.ui.theme.AmberSunContainer
import com.example.ui.theme.CardShape
import com.example.ui.theme.GardenSurface
import com.example.ui.theme.GardenTextMuted
import com.example.ui.theme.GardenTextPrimary
import com.example.ui.theme.GrowthGreen
import com.example.ui.theme.GrowthGreenContainer
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.SkyBlueWater

enum class TodayTaskType(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color
) {
    WATER(Icons.Default.WaterDrop, SkyBlueWater, SkyBlueContainer),
    SUNLIGHT(Icons.Default.WbSunny, AmberSun, AmberSunContainer),
    GROWTH(Icons.Default.Eco, GrowthGreen, GrowthGreenContainer)
}

data class TodayCareTask(
    val id: String,
    val title: String,
    val timeLabel: String,
    val type: TodayTaskType
)

@Composable
fun TodayCareTaskCard(
    task: TodayCareTask,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("today_task_${task.id}"),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = GardenSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Circle Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(task.type.iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = task.type.icon,
                        contentDescription = task.title,
                        tint = task.type.iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = GardenTextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = task.timeLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = GardenTextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
