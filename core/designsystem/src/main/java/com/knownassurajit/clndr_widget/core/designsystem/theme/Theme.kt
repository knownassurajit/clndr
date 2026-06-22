package com.knownassurajit.clndr_widget.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

enum class ThemeMode { FOLLOW_SYSTEM, FORCE_LIGHT, FORCE_DARK, SUNRISE_AUTO }

@Composable
fun ClndrTheme(
    mode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
    sunIsUp: Boolean = true,
    content: @Composable () -> Unit,
) {
    val useDark = when (mode) {
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeMode.FORCE_DARK -> true
        ThemeMode.FORCE_LIGHT -> false
        ThemeMode.SUNRISE_AUTO -> !sunIsUp
    }
    val palette = if (useDark) DarkPalette else LightPalette
    val colors: ColorScheme = if (useDark) DarkClndrColors else LightClndrColors
    CompositionLocalProvider(LocalClndrPalette provides palette) {
        MaterialTheme(
            colorScheme = colors,
            typography = ClndrTypography,
            shapes = ClndrShapes,
            content = content,
        )
    }
}
