package com.knownassurajit.clndr_widget.feature.widgets.life

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import com.knownassurajit.clndr_widget.feature.widgets.shared.W
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetBigNumber
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCaption
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetCard
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetEyebrow
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetSettings
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Mirrors the Life screen card: weeks lived + age, no remaining/lifespan. */
class LifeMatrixWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val birth = WidgetSettings.readBirthDate(context)
        val today = LocalDate.now()
        val weeksLived = birth?.let { ChronoUnit.DAYS.between(it, today).coerceAtLeast(0) / 7 }
        val age = birth?.let { ChronoUnit.YEARS.between(it, today).coerceAtLeast(0) }
        provideContent { LifeGlance(weeksLived, age) }
    }
}

@Composable
private fun LifeGlance(weeksLived: Long?, age: Long?) {
    WidgetCard {
        WidgetEyebrow("A life in weeks")
        Spacer(GlanceModifier.height(8.dp))
        if (weeksLived != null && age != null) {
            WidgetBigNumber(String.format(Locale.US, "%,d", weeksLived), suffix = "weeks lived")
            Spacer(GlanceModifier.height(8.dp))
            WidgetCaption("Age $age years")
        } else {
            WidgetCaption("Set your date of birth in clndr.")
        }
    }
}
