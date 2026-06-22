package com.knownassurajit.clndr_widget.feature.widgets.year

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBigNumber
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCaption
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCard
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetEyebrow
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetProgress
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.Year

/** Mirrors the Year screen card: days elapsed of the calendar year. */
class YearCalendarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val today = LocalDate.now()
        val total = if (Year.isLeap(today.year.toLong())) 366 else 365
        val elapsed = today.dayOfYear
        provideContent { YearCalendarGlance(today.year, elapsed, total) }
    }
}

@Composable
private fun YearCalendarGlance(year: Int, elapsed: Int, total: Int) {
    val pct = elapsed.toDouble() / total
    WidgetCard {
        WidgetEyebrow("The year · $year")
        Spacer(GlanceModifier.height(6.dp))
        WidgetBigNumber("$elapsed", suffix = "of $total days")
        Spacer(GlanceModifier.height(10.dp))
        WidgetProgress(pct.toFloat())
        Spacer(GlanceModifier.height(8.dp))
        WidgetCaption("${"%.1f".format(pct * 100)}% spent · ${total - elapsed} days remaining")
    }
}

@AndroidEntryPoint
class YearCalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = YearCalendarWidget()
}
