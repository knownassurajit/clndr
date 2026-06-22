package com.knownassurajit.clndr_widget.feature.widgets.shared

import android.content.Context
import androidx.glance.color.ColorProviders
import com.knownassurajit.clndr_widget.core.designsystem.theme.DarkClndrColors
import com.knownassurajit.clndr_widget.core.designsystem.theme.LightClndrColors

/**
 * Glance maps M3 ColorSchemes via ColorProviders. This indirection is the single
 * source of truth — no MaterialTheme inside widgets.
 */
object WidgetTheme {

    fun colors(@Suppress("UNUSED_PARAMETER") context: Context): ColorProviders =
        androidx.glance.material3.ColorProviders(light = LightClndrColors, dark = DarkClndrColors)
}
