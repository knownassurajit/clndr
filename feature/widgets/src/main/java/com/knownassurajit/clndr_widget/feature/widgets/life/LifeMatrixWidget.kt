package com.knownassurajit.clndr_widget.feature.widgets.life

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import com.knownassurajit.clndr_widget.feature.widgets.shared.LocalWidgetColors
import com.knownassurajit.clndr_widget.feature.widgets.shared.ProvideWidgetColors
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBigNumber
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBucket
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCaption
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCard
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetEyebrow
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetSettings
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetSizeModes
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetTheme
import com.knownassurajit.clndr_widget.feature.widgets.shared.currentWidgetBucket
import com.knownassurajit.clndr_widget.feature.widgets.shared.openApp
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Mirrors the Life screen: weeks lived + age, with a year mini-grid when space allows. */
class LifeMatrixWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(WidgetSizeModes.All)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val birth = WidgetSettings.readBirthDate(context)
        val today = LocalDate.now()
        val weeksLived = birth?.let { ChronoUnit.DAYS.between(it, today).coerceAtLeast(0) / 7 }
        val age = birth?.let { ChronoUnit.YEARS.between(it, today).coerceAtLeast(0) }
        val colors = WidgetTheme.colors(context)
        provideContent {
            ProvideWidgetColors(colors) {
                LifeGlance(weeksLived, age, GlanceModifier.openApp(context))
            }
        }
    }
}

@Composable
private fun LifeGlance(weeksLived: Long?, age: Long?, modifier: GlanceModifier) {
    WidgetCard(modifier) {
        WidgetEyebrow("A life in weeks")
        Spacer(GlanceModifier.height(8.dp))
        if (weeksLived == null || age == null) {
            WidgetCaption("Set your date of birth in clndr.")
            return@WidgetCard
        }
        when (currentWidgetBucket()) {
            WidgetBucket.Compact -> {
                WidgetBigNumber(String.format(Locale.US, "%,d", weeksLived))
                WidgetCaption("weeks lived")
            }
            WidgetBucket.Medium -> {
                WidgetBigNumber(String.format(Locale.US, "%,d", weeksLived), suffix = "weeks lived")
                Spacer(GlanceModifier.height(8.dp))
                WidgetCaption("Age $age years")
            }
            WidgetBucket.Expanded -> {
                WidgetBigNumber(String.format(Locale.US, "%,d", weeksLived), suffix = "weeks lived")
                Spacer(GlanceModifier.height(8.dp))
                WidgetCaption("Age $age years")
                Spacer(GlanceModifier.height(10.dp))
                YearMiniGrid(ageYears = age.toInt())
            }
        }
    }
}

@Composable
private fun YearMiniGrid(ageYears: Int, columns: Int = 10) {
    val colors = LocalWidgetColors.current
    val total = (ageYears + 15).coerceIn(columns, 50)
    val rows = (total + columns - 1) / columns
    Column {
        repeat(rows) { row ->
            Row {
                repeat(columns) { col ->
                    val index = row * columns + col
                    if (index < total) {
                        val lived = index < ageYears
                        Spacer(
                            GlanceModifier
                                .width(8.dp)
                                .height(8.dp)
                                .cornerRadius(1.dp)
                                .background(if (lived) colors.nodeLived else colors.nodeFuture),
                        )
                        if (col < columns - 1) Spacer(GlanceModifier.width(3.dp))
                    }
                }
            }
            if (row < rows - 1) Spacer(GlanceModifier.height(3.dp))
        }
    }
}
