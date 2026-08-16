package com.knownassurajit.clndr_widget.feature.widgets.shared

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.glance.unit.ColorProvider
import com.knownassurajit.clndr_widget.core.designsystem.theme.DarkPalette
import com.knownassurajit.clndr_widget.core.designsystem.theme.LightPalette

enum class WidgetColorMode { SYSTEM, LIGHT, DARK }

data class WidgetColors(
    val txtHi: ColorProvider,
    val txtMid: ColorProvider,
    val txtLow: ColorProvider,
    val surface: ColorProvider,
    val nodeLived: ColorProvider,
    val nodeFuture: ColorProvider,
)

private fun dayNight(light: Color, dark: Color): ColorProvider =
    androidx.glance.color.ColorProvider(day = light, night = dark)

private fun solid(color: Color): ColorProvider = dayNight(color, color)

object WidgetTheme {

    fun colors(mode: WidgetColorMode): WidgetColors = when (mode) {
        WidgetColorMode.SYSTEM -> WidgetColors(
            txtHi = dayNight(LightPalette.txtHi, DarkPalette.txtHi),
            txtMid = dayNight(LightPalette.txtMid, DarkPalette.txtMid),
            txtLow = dayNight(LightPalette.txtLow, DarkPalette.txtLow),
            surface = dayNight(LightPalette.surface, DarkPalette.surface),
            nodeLived = dayNight(LightPalette.nodeLived, DarkPalette.nodeLived),
            nodeFuture = dayNight(LightPalette.nodeFuture, DarkPalette.nodeFuture),
        )
        WidgetColorMode.LIGHT -> WidgetColors(
            txtHi = solid(LightPalette.txtHi),
            txtMid = solid(LightPalette.txtMid),
            txtLow = solid(LightPalette.txtLow),
            surface = solid(LightPalette.surface),
            nodeLived = solid(LightPalette.nodeLived),
            nodeFuture = solid(LightPalette.nodeFuture),
        )
        WidgetColorMode.DARK -> WidgetColors(
            txtHi = solid(DarkPalette.txtHi),
            txtMid = solid(DarkPalette.txtMid),
            txtLow = solid(DarkPalette.txtLow),
            surface = solid(DarkPalette.surface),
            nodeLived = solid(DarkPalette.nodeLived),
            nodeFuture = solid(DarkPalette.nodeFuture),
        )
    }

    fun colors(context: Context): WidgetColors = colors(WidgetSettings.colorMode(context))
}

internal val LocalWidgetColors = staticCompositionLocalOf {
    WidgetTheme.colors(WidgetColorMode.SYSTEM)
}

@Composable
internal fun ProvideWidgetColors(colors: WidgetColors, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalWidgetColors provides colors, content = content)
}
