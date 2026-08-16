package com.knownassurajit.clndr_widget.feature.widgets.shared

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.actionStartActivity
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

enum class WidgetBucket { Compact, Medium, Expanded }

@Composable
fun currentWidgetBucket(): WidgetBucket {
    val size = LocalSize.current
    return when {
        size.height < 140.dp || size.width < 160.dp -> WidgetBucket.Compact
        size.height < 220.dp && size.width < 280.dp -> WidgetBucket.Medium
        else -> WidgetBucket.Expanded
    }
}

object WidgetSizeModes {
    val Compact = DpSize(110.dp, 110.dp)
    val Medium = DpSize(180.dp, 110.dp)
    val Expanded = DpSize(250.dp, 180.dp)
    val Extra = DpSize(300.dp, 300.dp)
    val All = setOf(Compact, Medium, Expanded, Extra)
}

fun GlanceModifier.openApp(context: Context): GlanceModifier {
    val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
        ?: return this
    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return clickable(actionStartActivity(launch))
}

@Composable
fun WidgetCard(
    modifier: GlanceModifier = GlanceModifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalWidgetColors.current
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(colors.surface)
            .cornerRadius(20.dp)
            .padding(16.dp)
            .then(modifier),
    ) { content() }
}

@Composable
fun WidgetEyebrow(text: String) {
    val colors = LocalWidgetColors.current
    Text(
        text.uppercase(),
        style = TextStyle(color = colors.txtLow, fontSize = 10.sp, fontWeight = FontWeight.Medium),
    )
}

@Composable
fun WidgetBigNumber(value: String, suffix: String? = null) {
    val colors = LocalWidgetColors.current
    Row(verticalAlignment = Alignment.Bottom) {
        Text(value, style = TextStyle(color = colors.txtHi, fontSize = 34.sp, fontWeight = FontWeight.Bold))
        if (suffix != null) {
            Text(
                "  $suffix",
                style = TextStyle(color = colors.txtMid, fontSize = 13.sp, fontWeight = FontWeight.Medium),
            )
        }
    }
}

@Composable
fun WidgetProgress(fraction: Float) {
    val colors = LocalWidgetColors.current
    LinearProgressIndicator(
        progress = fraction.coerceIn(0f, 1f),
        modifier = GlanceModifier.fillMaxWidth().height(4.dp).cornerRadius(2.dp),
        color = colors.nodeLived,
        backgroundColor = colors.nodeFuture,
    )
}

@Composable
fun WidgetCycle(label: String, pct: Double) {
    val colors = LocalWidgetColors.current
    Column(GlanceModifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                style = TextStyle(color = colors.txtMid, fontSize = 12.sp, fontWeight = FontWeight.Medium),
                modifier = GlanceModifier.defaultWeight(),
            )
            Text(
                "${(pct * 100).toInt()}%",
                style = TextStyle(color = colors.txtHi, fontSize = 12.sp, fontWeight = FontWeight.Medium),
            )
        }
        Spacer(GlanceModifier.height(4.dp))
        WidgetProgress(pct.toFloat())
    }
}

@Composable
fun WidgetCaption(text: String) {
    val colors = LocalWidgetColors.current
    Text(text, style = TextStyle(color = colors.txtLow, fontSize = 11.sp))
}
