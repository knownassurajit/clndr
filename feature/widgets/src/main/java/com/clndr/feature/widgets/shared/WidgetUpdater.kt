package com.clndr.feature.widgets.shared

import android.content.Context
import com.clndr.feature.widgets.life.LifeMatrixWidget
import com.clndr.feature.widgets.year.YearProgressWidget

/**
 * Single entry point the rest of the app uses to redraw all widgets — e.g. on theme
 * change, sunrise/sunset crossing, or after a milestone is saved.
 */
object WidgetUpdater {

    suspend fun updateAll(context: Context) {
        YearProgressWidget().updateAll(context)
        LifeMatrixWidget().updateAll(context)
    }
}
