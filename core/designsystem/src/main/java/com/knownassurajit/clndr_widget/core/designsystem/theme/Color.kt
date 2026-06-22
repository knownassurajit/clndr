package com.knownassurajit.clndr_widget.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

/**
 * Material [androidx.compose.material3.ColorScheme]s mapped from the clndr palette so that
 * stock Material surfaces (modal sheets, switches, date pickers, default text) sit correctly
 * in the monochrome world. The richer token set lives in [ClndrPalette].
 *
 * `background`/`surface` map to the phone "screen" tier (the app canvas and sheets), while
 * cards paint the brighter `surface` tier explicitly via `MaterialTheme.clndr.surface`.
 */
val DarkClndrColors = darkColorScheme(
    primary = DarkPalette.nodeLived,
    onPrimary = DarkPalette.screen,
    primaryContainer = DarkPalette.surface2,
    onPrimaryContainer = DarkPalette.txtHi,
    secondary = DarkPalette.txtMid,
    onSecondary = DarkPalette.screen,
    secondaryContainer = DarkPalette.surface2,
    onSecondaryContainer = DarkPalette.txtHi,
    tertiary = DarkPalette.nodeRemain,
    onTertiary = DarkPalette.screen,
    background = DarkPalette.screen,
    onBackground = DarkPalette.txtHi,
    surface = DarkPalette.screen,
    onSurface = DarkPalette.txtHi,
    surfaceVariant = DarkPalette.surface2,
    onSurfaceVariant = DarkPalette.txtMid,
    surfaceContainer = DarkPalette.surface,
    surfaceContainerHigh = DarkPalette.surface2,
    outline = DarkPalette.nodeFuture,
    outlineVariant = DarkPalette.surface2,
    error = DarkPalette.txtHi,
    onError = DarkPalette.screen,
)

val LightClndrColors = lightColorScheme(
    primary = LightPalette.nodeLived,
    onPrimary = LightPalette.screen,
    primaryContainer = LightPalette.surface2,
    onPrimaryContainer = LightPalette.txtHi,
    secondary = LightPalette.txtMid,
    onSecondary = LightPalette.screen,
    secondaryContainer = LightPalette.surface2,
    onSecondaryContainer = LightPalette.txtHi,
    tertiary = LightPalette.nodeRemain,
    onTertiary = LightPalette.screen,
    background = LightPalette.screen,
    onBackground = LightPalette.txtHi,
    surface = LightPalette.screen,
    onSurface = LightPalette.txtHi,
    surfaceVariant = LightPalette.surface2,
    onSurfaceVariant = LightPalette.txtMid,
    surfaceContainer = LightPalette.surface,
    surfaceContainerHigh = LightPalette.surface2,
    outline = LightPalette.nodeFuture,
    outlineVariant = LightPalette.surface2,
    error = LightPalette.txtHi,
    onError = LightPalette.screen,
)
