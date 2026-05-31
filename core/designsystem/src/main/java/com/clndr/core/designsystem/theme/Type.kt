package com.clndr.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typeface stack — uses system fonts as a fallback so the module compiles without
 * font assets. The :app module wires in Poppins + JetBrains Mono via downloadable
 * font providers and replaces these default values via Typography composition.
 */
private val PoppinsFallback: FontFamily = FontFamily.SansSerif
private val MonoFallback: FontFamily = FontFamily.Monospace

val ClndrTypography = Typography(
    displayLarge = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W300, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W300, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W400, fontSize = 36.sp),
    headlineLarge = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W500, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W500, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W500, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W500, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W500, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W500, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W400, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W400, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = PoppinsFallback, fontWeight = FontWeight.W400, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = MonoFallback, fontWeight = FontWeight.W500, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = MonoFallback, fontWeight = FontWeight.W500, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = MonoFallback, fontWeight = FontWeight.W500, fontSize = 11.sp),
)

val NumericLarge = TextStyle(fontFamily = MonoFallback, fontWeight = FontWeight.W500, fontSize = 32.sp)
val NumericMedium = TextStyle(fontFamily = MonoFallback, fontWeight = FontWeight.W500, fontSize = 18.sp)
