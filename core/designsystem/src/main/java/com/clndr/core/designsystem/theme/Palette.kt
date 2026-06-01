package com.clndr.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The full clndr token set. Material's [androidx.compose.material3.ColorScheme] only has
 * slots for a handful of roles, but the cinematic-monochrome design (ported from clndr.html)
 * leans on a wider grey ramp: four text tiers, four surface tiers, and dedicated node colors
 * for the life-grid. We expose that ramp through [LocalClndrPalette] and read it with
 * `MaterialTheme.clndr`.
 *
 * Greys are the sRGB resolution of the original `oklch(L 0 0)` tokens; hairlines keep their
 * translucency so they read correctly over any surface tier.
 */
@Immutable
data class ClndrPalette(
    val bg: Color,
    val screen: Color,
    val surface: Color,
    val surface2: Color,
    val line: Color,
    val lineStrong: Color,
    val txtHi: Color,
    val txtMid: Color,
    val txtLow: Color,
    val txtFaint: Color,
    val nodeLived: Color,
    val nodeFuture: Color,
    val nodePast: Color,
    val nodeRemain: Color,
    val today: Color,
    val isDark: Boolean,
)

val DarkPalette = ClndrPalette(
    bg = Color(0xFF0A0A0A),
    screen = Color(0xFF0E0E0E),
    surface = Color(0xFF171717),
    surface2 = Color(0xFF202020),
    line = Color(0x17FFFFFF),
    lineStrong = Color(0x29FFFFFF),
    txtHi = Color(0xFFF7F7F7),
    txtMid = Color(0xFF989898),
    txtLow = Color(0xFF585858),
    txtFaint = Color(0xFF383838),
    nodeLived = Color(0xFFE8E8E8),
    nodeFuture = Color(0xFF333333),
    nodePast = Color(0xFF484848),
    nodeRemain = Color(0xFF868686),
    today = Color(0xFFFCFCFC),
    isDark = true,
)

val LightPalette = ClndrPalette(
    bg = Color(0xFFEEEEEE),
    screen = Color(0xFFFAFAFA),
    surface = Color(0xFFF3F3F3),
    surface2 = Color(0xFFE8E8E8),
    line = Color(0x1A000000),
    lineStrong = Color(0x33000000),
    txtHi = Color(0xFF0F0F0F),
    txtMid = Color(0xFF484848),
    txtLow = Color(0xFF747474),
    txtFaint = Color(0xFFABABAB),
    nodeLived = Color(0xFF121212),
    nodeFuture = Color(0xFFD1D1D1),
    nodePast = Color(0xFFBEBEBE),
    nodeRemain = Color(0xFF717171),
    today = Color(0xFF030303),
    isDark = false,
)

val LocalClndrPalette = staticCompositionLocalOf { DarkPalette }

/** Access the extended clndr token set, mirroring `MaterialTheme.colorScheme`. */
val MaterialTheme.clndr: ClndrPalette
    @ReadOnlyComposable
    @androidx.compose.runtime.Composable
    get() = LocalClndrPalette.current
