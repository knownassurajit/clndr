package com.clndr.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.clndr.core.designsystem.theme.ClndrText
import com.clndr.core.designsystem.theme.clndr

/** Small, all-caps tracked label — the recurring `.eyebrow` accent. */
@Composable
fun Eyebrow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.clndr.txtLow,
) {
    Text(text.uppercase(), modifier = modifier, style = ClndrText.eyebrow, color = color)
}

/** Section divider label (`.sub-head`). */
@Composable
fun SubHead(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(text.uppercase(), modifier = modifier, style = ClndrText.subHead, color = MaterialTheme.clndr.txtLow)
}

/** The surface card: brighter `surface` tier, hairline border, 20dp radius. */
@Composable
fun ClndrCard(
    modifier: Modifier = Modifier,
    padding: Dp = 18.dp,
    content: @Composable () -> Unit,
) {
    val palette = MaterialTheme.clndr
    val shape = MaterialTheme.shapes.medium
    Box(
        modifier
            .clip(shape)
            .background(palette.surface)
            .border(1.dp, palette.line, shape)
            .padding(padding),
    ) {
        content()
    }
}

/** Thin elapsed/remaining track (`.pline`). [fraction] is 0f..1f. */
@Composable
fun ClndrProgressLine(
    fraction: Float,
    modifier: Modifier = Modifier,
    height: Dp = 3.dp,
) {
    val palette = MaterialTheme.clndr
    val shape = RoundedCornerShape(2.dp)
    val f = fraction.coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(palette.nodeFuture),
    ) {
        Box(
            Modifier
                .fillMaxWidth(f)
                .fillMaxHeight()
                .clip(shape)
                .background(palette.nodeLived),
        )
    }
}

/** Equal-width segmented toggle (`.seg`). */
@Composable
fun SegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = MaterialTheme.clndr
    val outerShape = RoundedCornerShape(14.dp)
    val innerShape = RoundedCornerShape(10.dp)
    Row(
        modifier
            .fillMaxWidth()
            .clip(outerShape)
            .background(palette.surface)
            .border(1.dp, palette.line, outerShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEachIndexed { index, label ->
            val on = index == selectedIndex
            SegmentButton(
                label = label,
                on = on,
                onClick = { onSelect(index) },
                innerShape = innerShape,
                onColor = palette.txtHi,
                offColor = palette.txtLow,
                onBackground = palette.surface2,
            )
        }
    }
}

@Composable
private fun RowScope.SegmentButton(
    label: String,
    on: Boolean,
    onClick: () -> Unit,
    innerShape: RoundedCornerShape,
    onColor: Color,
    offColor: Color,
    onBackground: Color,
) {
    Box(
        Modifier
            .weight(1f)
            .clip(innerShape)
            .background(if (on) onBackground else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (on) onColor else offColor,
            fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}
