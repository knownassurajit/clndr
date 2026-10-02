package com.knownassurajit.clndr_widget.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode { FOLLOW_SYSTEM, FORCE_LIGHT, FORCE_DARK, SUNRISE_AUTO }

@Composable
fun ClndrTheme(
    mode: ThemeMode = ThemeMode.FOLLOW_SYSTEM,
    sunIsUp: Boolean = true,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val useDark = when (mode) {
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeMode.FORCE_DARK -> true
        ThemeMode.FORCE_LIGHT -> false
        ThemeMode.SUNRISE_AUTO -> !sunIsUp
    }

    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (useDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        useDark -> DarkClndrColors
        else -> LightClndrColors
    }

    val palette = if (useDark) DarkPalette else LightPalette

    CompositionLocalProvider(LocalClndrPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ClndrTypography,
            shapes = ClndrShapes,
            content = content,
        )
    }
}
