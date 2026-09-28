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
import androidx.compose.material.icons.filled.Balcony
import androidx.compose.material.icons.filled.Deck
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.data.model.Place
import com.example.ui.theme.CardShape
import com.example.ui.theme.GardenSurface
import com.example.ui.theme.GardenTextMuted
import com.example.ui.theme.GardenTextPrimary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.HighLightTag
import com.example.ui.theme.HighLightTagBg
import com.example.ui.theme.PillShape
import com.example.ui.theme.WarmTag
import com.example.ui.theme.WarmTagBg
import com.example.ui.theme.WindTag
import com.example.ui.theme.WindTagBg

data class EnvironmentalInfo(
    val subtitle: String,
    val icon: ImageVector,
    val lightTag: String,
    val windTag: String,
    val tempTag: String
)

fun getEnvironmentForPlace(placeName: String): EnvironmentalInfo {
    return when {
        placeName.contains("شرفة", ignoreCase = true) || placeName.contains("balcony", ignoreCase = true) -> {
            EnvironmentalInfo(
                subtitle = "شمس صباحية · 4-5 ساعات",
                icon = Icons.Default.Balcony,
                lightTag = "إضاءة عالية",
                windTag = "رياح معتدلة",
                tempTag = "دافئ"
            )
        }
        placeName.contains("سطح", ignoreCase = true) || placeName.contains("roof", ignoreCase = true) -> {
            EnvironmentalInfo(
                subtitle = "شمس مباشرة قوية طوال اليوم",
                icon = Icons.Default.Deck,
                lightTag = "إضاءة شمسية كاملة",
                windTag = "رياح نشطة",
                tempTag = "دافئ جداً"
            )
        }
        else -> {
            EnvironmentalInfo(
                subtitle = "إضاءة ساطعة غير مباشرة",
                icon = Icons.Default.Home,
                lightTag = "إضاءة غير مباشرة",
                windTag = "هواء هادئ",
                tempTag = "معتدل"
            )
        }
    }
}

@Composable
fun PlaceItemCard(
    place: Place,
    plantCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val env = getEnvironmentForPlace(place.name)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("place_card_${place.id}"),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = GardenSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = env.icon,
                            contentDescription = place.name,
                            tint = GreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GardenTextPrimary
                        )
                        Text(
                            text = env.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = GardenTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                // Plant Count Badge
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(GreenContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$plantCount نباتات",
                        style = MaterialTheme.typography.labelSmall,
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Environmental Tags Row (matching mockup)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MockupEnvironmentalTag(text = env.lightTag, color = HighLightTag, bg = HighLightTagBg)
                MockupEnvironmentalTag(text = env.windTag, color = WindTag, bg = WindTagBg)
                MockupEnvironmentalTag(text = env.tempTag, color = WarmTag, bg = WarmTagBg)
            }
        }
    }
}

@Composable
private fun MockupEnvironmentalTag(
    text: String,
    color: Color,
    bg: Color
) {
    Box(
        modifier = Modifier
            .clip(PillShape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}
