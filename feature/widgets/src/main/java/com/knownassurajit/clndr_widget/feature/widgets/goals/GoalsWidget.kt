package com.knownassurajit.clndr_widget.feature.widgets.goals

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.knownassurajit.clndr_widget.core.domain.model.Milestone
import com.knownassurajit.clndr_widget.core.domain.repository.MilestonesRepository
import com.knownassurajit.clndr_widget.feature.widgets.shared.LocalWidgetColors
import com.knownassurajit.clndr_widget.feature.widgets.shared.ProvideWidgetColors
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBigNumber
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBucket
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCaption
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCard
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetEyebrow
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetSizeModes
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetTheme
import com.knownassurajit.clndr_widget.feature.widgets.shared.currentWidgetBucket
import com.knownassurajit.clndr_widget.feature.widgets.shared.openApp
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

/** Mirrors the Goals screen: nearest milestone, plus the next few when space allows. */
class GoalsWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GoalsEntryPoint {
        fun milestonesRepository(): MilestonesRepository
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(WidgetSizeModes.All)

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val today = LocalDate.now()
        val upcoming: List<Milestone> = runCatching {
            val repo = EntryPointAccessors
                .fromApplication(context.applicationContext, GoalsEntryPoint::class.java)
                .milestonesRepository()
            val ahead = repo.observeUpcoming(today).first()
            if (ahead.isNotEmpty()) ahead.take(3)
            else repo.observePast(today).first().takeLast(1)
        }.getOrDefault(emptyList())
        val colors = WidgetTheme.colors(context)
        provideContent {
            ProvideWidgetColors(colors) {
                GoalsGlance(upcoming, today, GlanceModifier.openApp(context))
            }
        }
    }
}

private val DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)

@Composable
private fun GoalsGlance(items: List<Milestone>, today: LocalDate, modifier: GlanceModifier) {
    val colors = LocalWidgetColors.current
    WidgetCard(modifier) {
        val item = items.firstOrNull()
        if (item == null) {
            WidgetEyebrow("Goals")
            Spacer(GlanceModifier.height(8.dp))
            WidgetCaption("Anchor a milestone in clndr to start counting.")
            return@WidgetCard
        }
        val bucket = currentWidgetBucket()
        val days = ChronoUnit.DAYS.between(today, item.targetDate)
        val (badge, label) = when {
            days > 0 -> "Countdown" to "days remaining"
            days == 0L -> "Today" to "today"
            else -> "Count-up" to "days since"
        }
        WidgetEyebrow(badge)
        Spacer(GlanceModifier.height(6.dp))
        Text(
            item.title,
            style = TextStyle(color = colors.txtHi, fontSize = 17.sp, fontWeight = FontWeight.Bold),
        )
        WidgetCaption(item.targetDate.format(DATE_FMT))
        Spacer(GlanceModifier.height(10.dp))
        WidgetBigNumber(String.format(Locale.US, "%,d", abs(days)), suffix = label)
        if (bucket == WidgetBucket.Expanded && items.size > 1) {
            Spacer(GlanceModifier.height(12.dp))
            WidgetEyebrow("Next")
            items.drop(1).forEach { next ->
                val nDays = ChronoUnit.DAYS.between(today, next.targetDate)
                Spacer(GlanceModifier.height(6.dp))
                Text(
                    next.title,
                    style = TextStyle(color = colors.txtHi, fontSize = 13.sp, fontWeight = FontWeight.Medium),
                )
                WidgetCaption("${next.targetDate.format(DATE_FMT)} · ${abs(nDays)}d")
            }
        }
    }
}

@AndroidEntryPoint
class GoalsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = GoalsWidget()
}
