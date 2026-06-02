package com.clndr.feature.widgets.shared

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.clndr.feature.widgets.life.LifeMatrixWidget
import com.clndr.feature.widgets.year.YearProgressWidget

/**
 * Single entry point the rest of the app uses to redraw all widgets — e.g. on theme
 * change, sunrise/sunset crossing, or after a milestone is saved.
 */
object WidgetUpdater {

    suspend fun updateAll(context: Context) {
        if (isRunningUnitTest()) return
        YearProgressWidget().updateAll(context)
        LifeMatrixWidget().updateAll(context)
    }

    private fun isRunningUnitTest(): Boolean {
        return Thread.currentThread().stackTrace.any {
            it.className.contains("robolectric", ignoreCase = true) ||
            it.className.contains("junit", ignoreCase = true)
        }
    }
}
