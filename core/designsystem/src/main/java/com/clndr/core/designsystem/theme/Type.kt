package com.clndr.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Type system ported from clndr.html. The web design uses Poppins for UI and Oswald
 * (a tall, condensed face) for the big tabular numerals. We can't bundle/fetch those
 * binaries here, so we fall back to the platform sans family and recover the *feel*
 * with weight, tight tracking, and tabular figures. Swap [ClndrUi]/[ClndrNumerals] for
 * real font families (downloadable or bundled) to land the exact look.
 */
private val ClndrUi: FontFamily = FontFamily.SansSerif
private val ClndrNumerals: FontFamily = FontFamily.SansSerif

val ClndrTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = ClndrUi,
        fontWeight = FontWeight.SemiBold,
        fontSize = 57.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = ClndrUi,
        fontWeight = FontWeight.SemiBold,
        fontSize = 45.sp,
        letterSpacing = (-0.5).sp
    ),
    displaySmall = TextStyle(fontFamily = ClndrUi, fontWeight = FontWeight.SemiBold, fontSize = 36.sp),
    headlineLarge = TextStyle(
        fontFamily = ClndrUi,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ClndrUi,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = (-0.3).sp
    ),
    headlineSmall = TextStyle(fontFamily = ClndrUi, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleLarge = TextStyle(
        fontFamily = ClndrUi,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(fontFamily = ClndrUi, fontWeight = FontWeight.Medium, fontSize = 15.sp),
    titleSmall = TextStyle(fontFamily = ClndrUi, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = ClndrUi, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    bodyMedium = TextStyle(
        fontFamily = ClndrUi,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 21.sp
    ),
    bodySmall = TextStyle(fontFamily = ClndrUi, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = ClndrUi, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    labelMedium = TextStyle(fontFamily = ClndrUi, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = ClndrUi, fontWeight = FontWeight.Medium, fontSize = 11.sp),
)

/** Extra styles that don't map onto Material slots — eyebrows, sub-heads, and big numerals. */
object ClndrText {
    val eyebrow = TextStyle(
        fontFamily = ClndrUi,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
    )
    val subHead = TextStyle(
        fontFamily = ClndrUi,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 1.6.sp,
    )

    // Tabular numerals so live-updating figures don't shift width.
    val numHero = TextStyle(
        fontFamily = ClndrNumerals,
        fontWeight = FontWeight.SemiBold,
        fontSize = 78.sp,
        letterSpacing = (-1).sp,
        fontFeatureSettings = "tnum",
    )
    val numLarge = TextStyle(
        fontFamily = ClndrNumerals,
        fontWeight = FontWeight.SemiBold,
        fontSize = 46.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = "tnum",
    )
    val numMedium = TextStyle(
        fontFamily = ClndrNumerals,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        fontFeatureSettings = "tnum",
    )
    val numSmall = TextStyle(
        fontFamily = ClndrNumerals,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        fontFeatureSettings = "tnum",
    )
}

val NumericLarge = ClndrText.numMedium
val NumericMedium = ClndrText.numSmall
