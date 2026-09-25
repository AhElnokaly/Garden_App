package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.CareLog
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.SkyBlueWater
import com.example.ui.theme.SoilBrownContainer
import com.example.ui.theme.SoilBrownSecondary
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TerracottaContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CareLogItemCard(
    log: CareLog,
    modifier: Modifier = Modifier
) {
    val (icon, tintColor, bgContainer, title) = when (log.action_type) {
        "watered" -> Quadruple(Icons.Default.Opacity, SkyBlueWater, SkyBlueContainer, "سقاية وري بالماء")
        "fertilized" -> Quadruple(Icons.Default.Science, SoilBrownSecondary, SoilBrownContainer, "تسميد بالمغذيات")
        "photo" -> Quadruple(Icons.Default.CameraAlt, GreenPrimary, GreenContainer, "التقاط صورة للنمو")
        else -> Quadruple(Icons.Default.EditNote, Terracotta, TerracottaContainer, "ملاحظة عناية")
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(bgContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = formatLogTime(log.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!log.note_text.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = log.note_text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (log.action_type == "photo") {
                    Spacer(modifier = Modifier.height(6.dp))
                    val hasValidUri = !log.photo_uri.isNullOrBlank() && !log.photo_uri.startsWith("local_photo_")
                    if (hasValidUri) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(log.photo_uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "صورة النبتة المسجلة",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(MaterialTheme.shapes.extraSmall),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "📷 صورة موثقة في المعرض المحلي",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private fun formatLogTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val oneDay = 24 * 60 * 60 * 1000L
    return when {
        diff < 60 * 1000L -> "الآن"
        diff < 60 * 60 * 1000L -> "منذ ${(diff / (60 * 1000L))} دقيقة"
        diff < oneDay -> {
            val sdf = SimpleDateFormat("hh:mm a", Locale("ar"))
            "اليوم، ${sdf.format(Date(timestamp))}"
        }
        diff < 2 * oneDay -> "أمس"
        else -> {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale("ar"))
            sdf.format(Date(timestamp))
        }
    }
}
