package com.knownassurajit.clndr_widget.feature.widgets.year

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.knownassurajit.clndr_widget.feature.widgets.shared.LocalWidgetColors
import com.knownassurajit.clndr_widget.feature.widgets.shared.ProvideWidgetColors
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBigNumber
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBucket
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCaption
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCard
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetEyebrow
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetProgress
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetSizeModes
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetTheme
import com.knownassurajit.clndr_widget.feature.widgets.shared.currentWidgetBucket
import com.knownassurajit.clndr_widget.feature.widgets.shared.openApp
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

/** Mirrors the Year screen: days elapsed, plus a month matrix when space allows. */
class YearCalendarWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(WidgetSizeModes.All)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val today = LocalDate.now()
        val total = if (Year.isLeap(today.year.toLong())) 366 else 365
        val elapsed = today.dayOfYear
        val colors = WidgetTheme.colors(context)
        provideContent {
            ProvideWidgetColors(colors) {
                YearCalendarGlance(today, elapsed, total, GlanceModifier.openApp(context))
            }
        }
    }
}

@Composable
private fun YearCalendarGlance(today: LocalDate, elapsed: Int, total: Int, modifier: GlanceModifier) {
    val pct = elapsed.toDouble() / total
    WidgetCard(modifier) {
        when (currentWidgetBucket()) {
            WidgetBucket.Compact -> {
                WidgetEyebrow("${today.year}")
                Spacer(GlanceModifier.height(4.dp))
                WidgetBigNumber("$elapsed", suffix = "days")
            }
            WidgetBucket.Medium -> {
                WidgetEyebrow("The year · ${today.year}")
                Spacer(GlanceModifier.height(6.dp))
                WidgetBigNumber("$elapsed", suffix = "of $total days")
                Spacer(GlanceModifier.height(10.dp))
                WidgetProgress(pct.toFloat())
                Spacer(GlanceModifier.height(8.dp))
                WidgetCaption("${"%.1f".format(pct * 100)}% spent · ${total - elapsed} remaining")
            }
            WidgetBucket.Expanded -> {
                WidgetEyebrow("The year · ${today.year}")
                Spacer(GlanceModifier.height(6.dp))
                WidgetBigNumber("$elapsed", suffix = "of $total days")
                Spacer(GlanceModifier.height(8.dp))
                WidgetProgress(pct.toFloat())
                Spacer(GlanceModifier.height(10.dp))
                MonthStrip(today)
            }
        }
    }
}

private val MONTH_LETTERS = listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")

@Composable
private fun MonthStrip(today: LocalDate) {
    val colors = LocalWidgetColors.current
    Row(GlanceModifier.fillMaxWidth()) {
        MONTH_LETTERS.forEachIndexed { index, letter ->
            val month = index + 1
            val state = when {
                month < today.monthValue -> 1f
                month > today.monthValue -> 0f
                else -> today.dayOfMonth.toFloat() / YearMonth.of(today.year, month).lengthOfMonth()
            }
            Column(
                modifier = GlanceModifier.defaultWeight().padding(horizontal = 1.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    letter,
                    style = TextStyle(
                        color = if (month == today.monthValue) colors.txtHi else colors.txtLow,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(GlanceModifier.height(4.dp))
                Spacer(
                    GlanceModifier
                        .width(10.dp)
                        .height(18.dp)
                        .cornerRadius(2.dp)
                        .background(if (state > 0.5f) colors.nodeLived else colors.nodeFuture),
                )
            }
        }
    }
}

@AndroidEntryPoint
class YearCalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = YearCalendarWidget()
}
