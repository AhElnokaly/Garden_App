package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = OnGreenPrimary,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenPrimaryDark,
    secondary = SoilBrownSecondary,
    onSecondary = OnSoilBrown,
    secondaryContainer = SoilBrownContainer,
    onSecondaryContainer = SoilBrownSecondary,
    tertiary = SkyBlueWater,
    onTertiary = OnSkyBlue,
    tertiaryContainer = SkyBlueContainer,
    onTertiaryContainer = SkyBlueWater,
    background = GardenBackground,
    onBackground = GardenTextPrimary,
    surface = GardenSurface,
    onSurface = GardenTextPrimary,
    surfaceVariant = GardenSurfaceVariant,
    onSurfaceVariant = GardenTextSecondary,
    outline = GardenOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimaryDarkTheme,
    onPrimary = GreenPrimaryDark,
    primaryContainer = GreenPrimaryDark,
    onPrimaryContainer = GreenContainer,
    secondary = SoilBrownDarkTheme,
    onSecondary = SoilBrownSecondary,
    secondaryContainer = SoilBrownSecondary,
    onSecondaryContainer = SoilBrownContainer,
    tertiary = SkyBlueDarkTheme,
    onTertiary = SkyBlueWater,
    tertiaryContainer = SkyBlueWater,
    onTertiaryContainer = SkyBlueContainer,
    background = GardenBackgroundDark,
    onBackground = GardenTextPrimaryDark,
    surface = GardenSurfaceDark,
    onSurface = GardenTextPrimaryDark,
    surfaceVariant = GardenSurfaceVariantDark,
    onSurfaceVariant = GardenTextSecondaryDark,
    outline = GardenOutline
)

@Composable
fun GardenCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep brand natural green by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Force RTL for Arabic first experience
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
