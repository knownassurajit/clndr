package com.knownassurajit.clndr_widget.feature.widgets.shared

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.knownassurajit.clndr_widget.feature.widgets.goals.GoalsWidget
import com.knownassurajit.clndr_widget.feature.widgets.life.LifeMatrixWidget
import com.knownassurajit.clndr_widget.feature.widgets.year.YearCalendarWidget
import com.knownassurajit.clndr_widget.feature.widgets.year.YearProgressWidget

/**
 * Single entry point the rest of the app uses to redraw all widgets — e.g. on theme
 * change, sunrise/sunset crossing, or after a milestone is saved.
 */
object WidgetUpdater {

    suspend fun updateAll(context: Context) {
        if (isRunningUnitTest()) return
        YearProgressWidget().updateAll(context)
        YearCalendarWidget().updateAll(context)
        LifeMatrixWidget().updateAll(context)
        GoalsWidget().updateAll(context)
    }

    private fun isRunningUnitTest(): Boolean {
        return Thread.currentThread().stackTrace.any {
            it.className.contains("robolectric", ignoreCase = true) ||
            it.className.contains("junit", ignoreCase = true)
        }
    }
}
