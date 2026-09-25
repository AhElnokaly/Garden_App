package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.SkyBlueWater
import com.example.ui.theme.SoilBrownSecondary
import com.example.ui.theme.Terracotta

@Composable
fun QuickCareActionsGrid(
    onWater: () -> Unit,
    onFertilize: () -> Unit,
    onPhoto: () -> Unit,
    onNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(
            label = "سقيت 💧",
            containerColor = SkyBlueWater,
            icon = Icons.Default.Opacity,
            onClick = onWater,
            modifier = Modifier
                .weight(1f)
                .testTag("action_water_button")
        )

        QuickActionButton(
            label = "سمّدت 🧪",
            containerColor = SoilBrownSecondary,
            icon = Icons.Default.Science,
            onClick = onFertilize,
            modifier = Modifier
                .weight(1f)
                .testTag("action_fertilize_button")
        )

        QuickActionButton(
            label = "صوّرت 📸",
            containerColor = GreenPrimary,
            icon = Icons.Default.CameraAlt,
            onClick = onPhoto,
            modifier = Modifier
                .weight(1f)
                .testTag("action_photo_button")
        )

        QuickActionButton(
            label = "ملاحظة 📝",
            containerColor = Terracotta,
            icon = Icons.Default.EditNote,
            onClick = onNote,
            modifier = Modifier
                .weight(1f)
                .testTag("action_note_button")
        )
    }
}

@Composable
fun QuickActionButton(
    label: String,
    containerColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
