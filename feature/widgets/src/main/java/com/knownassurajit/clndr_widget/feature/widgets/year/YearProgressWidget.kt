package com.knownassurajit.clndr_widget.feature.widgets.year

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import com.knownassurajit.clndr_widget.core.datetime.ProgressBuckets
import com.knownassurajit.clndr_widget.core.datetime.ProgressEngine
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBigNumber
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCard
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCycle
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetEyebrow
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetProgress
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetSettings
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Mirrors the Progress screen: year-elapsed hero + concurrent cycles. */
class YearProgressWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val buckets = ProgressEngine().compute(
            now = Instant.now(),
            zone = ZoneId.systemDefault(),
            birthDate = WidgetSettings.readBirthDate(context),
        )
        provideContent { YearProgressGlance(buckets, LocalDate.now().year) }
    }
}

@Composable
private fun YearProgressGlance(b: ProgressBuckets, year: Int) {
    WidgetCard {
        WidgetEyebrow("$year in progress")
        Spacer(GlanceModifier.height(6.dp))
        WidgetBigNumber("${"%.1f".format(b.yearPct * 100)}%", suffix = "elapsed")
        Spacer(GlanceModifier.height(10.dp))
        WidgetProgress(b.yearPct.toFloat())
        Spacer(GlanceModifier.height(10.dp))
        WidgetCycle("Decade", b.decadePct)
        WidgetCycle("Month", b.monthPct)
        WidgetCycle("Week", b.weekPct)
        WidgetCycle("Day", b.dayPct)
    }
}
