package com.knownassurajit.clndr_widget.feature.widgets.year

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import com.knownassurajit.clndr_widget.core.datetime.ProgressBuckets
import com.knownassurajit.clndr_widget.core.datetime.ProgressEngine
import com.knownassurajit.clndr_widget.feature.widgets.shared.ProvideWidgetColors
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBigNumber
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBucket
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCard
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCycle
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetEyebrow
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetProgress
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetSettings
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetSizeModes
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetTheme
import com.knownassurajit.clndr_widget.feature.widgets.shared.currentWidgetBucket
import com.knownassurajit.clndr_widget.feature.widgets.shared.openApp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Mirrors the Progress screen: year-elapsed hero + concurrent cycles. */
class YearProgressWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(WidgetSizeModes.All)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val buckets = ProgressEngine().compute(
            now = Instant.now(),
            zone = ZoneId.systemDefault(),
            birthDate = WidgetSettings.readBirthDate(context),
        )
        val colors = WidgetTheme.colors(context)
        val year = LocalDate.now().year
        provideContent {
            ProvideWidgetColors(colors) {
                YearProgressGlance(
                    buckets = buckets,
                    year = year,
                    modifier = GlanceModifier.openApp(context),
                )
            }
        }
    }
}

@Composable
private fun YearProgressGlance(
    buckets: ProgressBuckets,
    year: Int,
    modifier: GlanceModifier = GlanceModifier,
) {
    WidgetCard(modifier) {
        when (currentWidgetBucket()) {
            WidgetBucket.Compact -> {
                WidgetEyebrow("$year")
                Spacer(GlanceModifier.height(4.dp))
                WidgetBigNumber("${"%.0f".format(buckets.yearPct * 100)}%", suffix = "year")
            }
            WidgetBucket.Medium -> {
                WidgetEyebrow("$year in progress")
                Spacer(GlanceModifier.height(6.dp))
                WidgetBigNumber("${"%.1f".format(buckets.yearPct * 100)}%", suffix = "elapsed")
                Spacer(GlanceModifier.height(10.dp))
                WidgetProgress(buckets.yearPct.toFloat())
                Spacer(GlanceModifier.height(10.dp))
                WidgetCycle("Month", buckets.monthPct)
                WidgetCycle("Day", buckets.dayPct)
            }
            WidgetBucket.Expanded -> {
                WidgetEyebrow("$year in progress")
                Spacer(GlanceModifier.height(6.dp))
                WidgetBigNumber("${"%.1f".format(buckets.yearPct * 100)}%", suffix = "elapsed")
                Spacer(GlanceModifier.height(10.dp))
                WidgetProgress(buckets.yearPct.toFloat())
                Spacer(GlanceModifier.height(10.dp))
                WidgetCycle("Era", buckets.eraPct)
                WidgetCycle("Decade", buckets.decadePct)
                WidgetCycle("Month", buckets.monthPct)
                WidgetCycle("Week", buckets.weekPct)
                WidgetCycle("Day", buckets.dayPct)
            }
        }
    }
}
