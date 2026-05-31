package com.clndr.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Black0 = Color(0xFF000000)
val Black10 = Color(0xFF0A0A0A)
val Black20 = Color(0xFF141414)
val Gray30 = Color(0xFF1F1F1F)
val Gray50 = Color(0xFF333333)
val Gray70 = Color(0xFF808080)
val Gray85 = Color(0xFFB3B3B3)
val Gray95 = Color(0xFFE6E6E6)
val White100 = Color(0xFFFFFFFF)

val DarkClndrColors = darkColorScheme(
    primary = White100,
    onPrimary = Black0,
    primaryContainer = Gray30,
    onPrimaryContainer = Gray95,
    secondary = Gray85,
    onSecondary = Black0,
    secondaryContainer = Gray30,
    onSecondaryContainer = Gray95,
    tertiary = Gray70,
    onTertiary = Black0,
    background = Black0,
    onBackground = White100,
    surface = Black10,
    onSurface = Gray95,
    surfaceVariant = Gray30,
    onSurfaceVariant = Gray85,
    outline = Gray50,
    outlineVariant = Gray30,
)

val LightClndrColors = lightColorScheme(
    primary = Black0,
    onPrimary = White100,
    primaryContainer = Gray85,
    onPrimaryContainer = Black10,
    secondary = Gray50,
    onSecondary = White100,
    secondaryContainer = Gray85,
    onSecondaryContainer = Black10,
    tertiary = Gray70,
    onTertiary = White100,
    background = White100,
    onBackground = Black0,
    surface = Gray95,
    onSurface = Black20,
    surfaceVariant = Gray85,
    onSurfaceVariant = Gray50,
    outline = Gray70,
    outlineVariant = Gray85,
)
