package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSun
import com.example.ui.theme.AmberSunContainer
import com.example.ui.theme.CardShape
import com.example.ui.theme.GardenOutline
import com.example.ui.theme.GardenSurface
import com.example.ui.theme.GardenTextMuted
import com.example.ui.theme.GardenTextPrimary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.GrowthGreen
import com.example.ui.theme.GrowthGreenContainer
import com.example.ui.theme.PillShape

@Composable
fun PlantGrowthGauge(
    percentage: Int = 68,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "growth_progress"
    )

    Box(
        modifier = modifier
            .size(130.dp)
            .testTag("plant_growth_gauge"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val strokeWidth = 10.dp.toPx()
            // Background track
            drawCircle(
                color = GreenContainer,
                style = Stroke(width = strokeWidth)
            )
            // Progress arc
            drawArc(
                color = GreenPrimary,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = GardenTextPrimary,
                fontSize = 24.sp
            )
            Text(
                text = "معدل النمو",
                style = MaterialTheme.typography.labelSmall,
                color = GardenTextMuted,
                fontSize = 11.sp
            )
        }
    }
}

data class GrowthMilestone(
    val name: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

@Composable
fun PlantStageTimeline(
    stageName: String = "نمو خضري صحي",
    nextMilestoneTitle: String = "نمو 4 ورقات إضافية",
    estimatedDays: String = "خلال 5-8 أيام",
    milestones: List<GrowthMilestone> = listOf(
        GrowthMilestone("إنبات", isCompleted = true, isCurrent = false),
        GrowthMilestone("أول أوراق", isCompleted = true, isCurrent = false),
        GrowthMilestone("4 ورقات", isCompleted = true, isCurrent = false),
        GrowthMilestone("تفرع كثيف", isCompleted = false, isCurrent = true),
        GrowthMilestone("إزهار", isCompleted = false, isCurrent = false)
    ),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("plant_stage_timeline"),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = GardenSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stage header
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "المرحلة الحالية",
                    style = MaterialTheme.typography.labelMedium,
                    color = GardenTextMuted
                )
                Text(
                    text = stageName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            }

            // Timeline Steps Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                milestones.forEachIndexed { index, milestone ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Node Circle
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        milestone.isCompleted -> GreenPrimary
                                        milestone.isCurrent -> GrowthGreen
                                        else -> GardenOutline
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (milestone.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = milestone.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (milestone.isCurrent || milestone.isCompleted) GardenTextPrimary else GardenTextMuted,
                            fontWeight = if (milestone.isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Next Milestone Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(GrowthGreenContainer)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = GrowthGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "الهدف القادم: $nextMilestoneTitle",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GardenTextPrimary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "الوقت المتوقع: $estimatedDays",
                        style = MaterialTheme.typography.bodySmall,
                        color = GardenTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun GardenMemoryCard(
    note: String = "تنمو هذه النبتة بأفضل صورة في الأماكن ذات شمس الصباح مع ري معتدل كل 2-3 أيام.",
    confidence: String = "دقة الملاحظة: عالية 🌿",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("garden_memory_card"),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = AmberSunContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AmberSun,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "ذاكرة الحديقة الزراعية 💡",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = GardenTextPrimary
                )
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = GardenTextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
                Text(
                    text = confidence,
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberSun,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}
