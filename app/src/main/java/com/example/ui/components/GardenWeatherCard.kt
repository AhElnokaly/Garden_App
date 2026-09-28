package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.PillShape

@Composable
fun GardenWeatherCard(
    temperature: String = "31°C",
    condition: String = "مشمس",
    conditionNote: String = "ظروف نمو ممتازة",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("garden_weather_card"),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = GardenSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Sun Icon Container
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(AmberSunContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "حالة الطقس",
                        tint = AmberSun,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "اليوم",
                            style = MaterialTheme.typography.bodySmall,
                            color = GardenTextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = temperature,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GardenTextPrimary
                        )
                    }
                    Text(
                        text = conditionNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = GardenTextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // Condition Tag (e.g. Sunny / مشمس)
            Box(
                modifier = Modifier
                    .clip(PillShape)
                    .background(AmberSunContainer)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = condition,
                    style = MaterialTheme.typography.labelMedium,
                    color = AmberSun,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
