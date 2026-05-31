package com.clndr.feature.widgets.life

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import com.clndr.core.datetime.Granularity
import com.clndr.core.datetime.LifeGridCalculator
import com.clndr.core.datetime.LifeGridSpec
import com.clndr.core.designsystem.theme.DarkClndrColors
import com.clndr.core.designsystem.theme.LightClndrColors
import com.clndr.feature.widgets.shared.WidgetSettings
import java.time.LocalDate

class LifeMatrixWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val birth = WidgetSettings.readBirthDate(context)
        val today = LocalDate.now()
        val (current, total) = if (birth != null) {
            val spec = LifeGridSpec(birthDate = birth, granularity = Granularity.WEEKS, today = today)
            val c = LifeGridCalculator()
            c.currentIndex(spec) to c.totalCells(spec)
        } else {
            0 to 0
        }
        provideContent {
            androidx.glance.GlanceTheme(
                colors = ColorProviders(light = LightClndrColors, dark = DarkClndrColors),
            ) {
                LifeMatrixGlance(current = current, total = total)
            }
        }
    }
}

@Composable
private fun LifeMatrixGlance(current: Int, total: Int) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(androidx.glance.GlanceTheme.colors.background)
            .padding(12.dp),
    ) {
        Text(
            if (total > 0) "Weeks lived: $current of $total"
            else "Set your date of birth in clndr to render the life matrix.",
        )
    }
}
