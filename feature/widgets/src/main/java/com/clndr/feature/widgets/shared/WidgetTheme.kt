package com.clndr.feature.widgets.shared

import android.content.Context
import androidx.glance.material3.ColorProviders
import com.clndr.core.designsystem.theme.DarkClndrColors
import com.clndr.core.designsystem.theme.LightClndrColors

/**
 * Glance maps M3 ColorSchemes via ColorProviders. This indirection is the single
 * source of truth — no MaterialTheme inside widgets.
 */
object WidgetTheme {

    fun colors(@Suppress("UNUSED_PARAMETER") context: Context): ColorProviders =
        ColorProviders(light = LightClndrColors, dark = DarkClndrColors)
}
