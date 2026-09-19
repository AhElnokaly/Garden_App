package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.SkyBlueContainer
import com.example.ui.theme.SoilBrownContainer

@Composable
fun PlantAvatar(
    category: String,
    iconRef: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    shapeRadius: Dp = 16.dp
) {
    val (backgroundColor, emoji) = when (category) {
        "herb" -> GreenContainer to when {
            iconRef.contains("mint") -> "🌿"
            iconRef.contains("basil") -> "🌱"
            iconRef.contains("rosemary") -> "🌾"
            else -> "🌿"
        }
        "vegetable" -> SoilBrownContainer to when {
            iconRef.contains("tomato") -> "🍅"
            iconRef.contains("lemon") -> "🍋"
            iconRef.contains("chili") -> "🌶️"
            else -> "🪴"
        }
        else -> SkyBlueContainer to when {
            iconRef.contains("rose") -> "🌹"
            iconRef.contains("lily") -> "🌸"
            iconRef.contains("aloe") || iconRef.contains("snake") -> "🌵"
            else -> "🪴"
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(shapeRadius))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = (size.value * 0.45).sp
        )
    }
}

@Composable
fun PlantCategoryBadge(
    category: String,
    modifier: Modifier = Modifier
) {
    val (label, bg, textCol) = when (category) {
        "herb" -> Triple("عشبية", GreenContainer, GreenPrimary)
        "vegetable" -> Triple("خضار / ثمار", SoilBrownContainer, Color(0xFF5D4037))
        else -> Triple("زينة", SkyBlueContainer, Color(0xFF0277BD))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textCol
        )
    }
}
