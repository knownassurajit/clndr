package com.knownassurajit.clndr_widget.feature.widgets.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.color.ColorProvider
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.knownassurajit.clndr_widget.core.designsystem.theme.DarkPalette
import com.knownassurajit.clndr_widget.core.designsystem.theme.LightPalette

/**
 * Glance can't share Compose UI directly, so this is the widget-side mirror of the
 * design system: the same monochrome [com.knownassurajit.clndr_widget.core.designsystem.theme.ClndrPalette]
 * tokens, the same card / eyebrow / progress-line primitives the in-app screens use.
 */
object W {
    private fun cp(light: Color, dark: Color) = ColorProvider(day = light, night = dark)
    val txtHi = cp(LightPalette.txtHi, DarkPalette.txtHi)
    val txtMid = cp(LightPalette.txtMid, DarkPalette.txtMid)
    val txtLow = cp(LightPalette.txtLow, DarkPalette.txtLow)
    val surface = cp(LightPalette.surface, DarkPalette.surface)
    val nodeLived = cp(LightPalette.nodeLived, DarkPalette.nodeLived)
    val nodeFuture = cp(LightPalette.nodeFuture, DarkPalette.nodeFuture)
}

/** The surface card filling the widget — brighter surface tier, 20dp radius. */
@Composable
fun WidgetCard(content: @Composable () -> Unit) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(W.surface)
            .cornerRadius(20.dp)
            .padding(16.dp),
    ) { content() }
}

@Composable
fun WidgetEyebrow(text: String) {
    Text(
        text.uppercase(),
        style = TextStyle(color = W.txtLow, fontSize = 10.sp, fontWeight = FontWeight.Medium),
    )
}

@Composable
fun WidgetBigNumber(value: String, suffix: String? = null) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(value, style = TextStyle(color = W.txtHi, fontSize = 34.sp, fontWeight = FontWeight.Bold))
        if (suffix != null) {
            Text(
                "  $suffix",
                style = TextStyle(color = W.txtMid, fontSize = 13.sp, fontWeight = FontWeight.Medium),
            )
        }
    }
}

@Composable
fun WidgetProgress(fraction: Float) {
    LinearProgressIndicator(
        progress = fraction.coerceIn(0f, 1f),
        modifier = GlanceModifier.fillMaxWidth().height(4.dp).cornerRadius(2.dp),
        color = W.nodeLived,
        backgroundColor = W.nodeFuture,
    )
}

/** A labelled cycle row: name on the left, percentage on the right, track beneath. */
@Composable
fun WidgetCycle(label: String, pct: Double) {
    Column(GlanceModifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                style = TextStyle(color = W.txtMid, fontSize = 12.sp, fontWeight = FontWeight.Medium),
                modifier = GlanceModifier.defaultWeight(),
            )
            Text(
                "${(pct * 100).toInt()}%",
                style = TextStyle(color = W.txtHi, fontSize = 12.sp, fontWeight = FontWeight.Medium),
            )
        }
        Spacer(GlanceModifier.height(4.dp))
        WidgetProgress(pct.toFloat())
    }
}

@Composable
fun WidgetCaption(text: String) {
    Text(text, style = TextStyle(color = W.txtLow, fontSize = 11.sp))
}
