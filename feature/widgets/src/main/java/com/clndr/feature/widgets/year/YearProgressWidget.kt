package com.clndr.feature.widgets.year

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import com.clndr.core.datetime.ProgressBuckets
import com.clndr.core.datetime.ProgressEngine
import com.clndr.core.designsystem.theme.DarkClndrColors
import com.clndr.core.designsystem.theme.LightClndrColors
import com.clndr.feature.widgets.shared.WidgetSettings
import java.time.Instant
import java.time.ZoneId

class YearProgressWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val buckets = ProgressEngine().compute(
            now = Instant.now(),
            zone = ZoneId.systemDefault(),
            birthDate = WidgetSettings.readBirthDate(context),
        )
        provideContent {
            androidx.glance.GlanceTheme(
                colors = ColorProviders(light = LightClndrColors, dark = DarkClndrColors),
            ) {
                YearProgressGlance(buckets)
            }
        }
    }
}

@Composable
private fun YearProgressGlance(b: ProgressBuckets) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(androidx.glance.GlanceTheme.colors.background)
            .padding(12.dp),
    ) {
        Row("ERA", b.eraPct)
        Spacer(GlanceModifier.height(4.dp))
        Row("YEAR", b.yearPct)
        Spacer(GlanceModifier.height(4.dp))
        Row("MONTH", b.monthPct)
        Spacer(GlanceModifier.height(4.dp))
        Row("WEEK", b.weekPct)
        Spacer(GlanceModifier.height(4.dp))
        Row("DAY", b.dayPct)
    }
}

@Composable
private fun Row(label: String, value: Double) {
    val pct = value.coerceIn(0.0, 1.0)
    Text("$label  ${(pct * 100).toInt()}%")
    LinearProgressIndicator(progress = pct.toFloat())
}
