package com.knownassurajit.clndr_widget.feature.lifegrid.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.knownassurajit.clndr_widget.core.datetime.CellState
import com.knownassurajit.clndr_widget.core.datetime.LifeGridCalculator
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Renders the Days granularity life grid using a single Canvas — no per-cell composables.
 * Layout: each row holds DAYS_PER_ROW cells (one year ≈ 365 cells), wrapping vertically.
 * Pan/zoom updates a float-state offset + scale that only triggers a Canvas redraw.
 */
@Composable
fun DaysCanvasGrid(
    packed: IntArray,
    currentIndex: Int,
    totalCells: Int,
    modifier: Modifier = Modifier,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val infinite = rememberInfiniteTransition(label = "presentPulse")
    val pulseAlpha by infinite.animateFloat(
        initialValue = MIN_PULSE_ALPHA,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(PULSE_DURATION_MS), RepeatMode.Reverse),
        label = "pulseAlpha",
    )

    val pastColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline
    val presentColor = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.surface

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            },
    ) {
        if (totalCells <= 0) return@Canvas
        drawGrid(
            packed = packed,
            currentIndex = currentIndex,
            totalCells = totalCells,
            scale = scale,
            offset = Offset(offsetX, offsetY),
            canvas = size,
            density = this,
            pastColor = pastColor,
            outlineColor = outlineColor,
            presentColor = presentColor.copy(alpha = pulseAlpha),
        )
    }
}

@Suppress("LongParameterList")
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGrid(
    packed: IntArray,
    currentIndex: Int,
    totalCells: Int,
    scale: Float,
    offset: Offset,
    canvas: Size,
    density: Density,
    pastColor: Color,
    outlineColor: Color,
    presentColor: Color,
) {
    val cellPx = with(density) { (BASE_CELL_DP * scale).dp.toPx() }
    val gapPx = with(density) { (BASE_GAP_DP * scale).dp.toPx() }
    val pitch = cellPx + gapPx
    if (pitch <= 0f) return

    val cols = DAYS_PER_ROW
    val firstCol = max(0, floor(-offset.x / pitch).toInt())
    val lastCol = min(cols - 1, ceil((canvas.width - offset.x) / pitch).toInt())
    val firstRow = max(0, floor(-offset.y / pitch).toInt())
    val lastRow = ceil((canvas.height - offset.y) / pitch).toInt()

    for (row in firstRow..lastRow) {
        for (col in firstCol..lastCol) {
            val index = row * cols + col
            if (index >= totalCells) return
            val x = offset.x + col * pitch
            val y = offset.y + row * pitch
            val state = LifeGridCalculator.unpackState(packed, index)
            when (state) {
                CellState.PAST -> drawRect(
                    color = pastColor,
                    topLeft = Offset(x, y),
                    size = Size(cellPx, cellPx),
                )
                CellState.FUTURE -> drawRect(
                    color = outlineColor,
                    topLeft = Offset(x, y),
                    size = Size(cellPx, cellPx),
                    style = Stroke(width = STROKE_WIDTH_PX),
                )
                CellState.PRESENT -> drawRect(
                    color = presentColor,
                    topLeft = Offset(x, y),
                    size = Size(cellPx, cellPx),
                )
            }
            if (index == currentIndex && state != CellState.PRESENT) {
                drawRect(
                    color = presentColor,
                    topLeft = Offset(x, y),
                    size = Size(cellPx, cellPx),
                    style = Stroke(width = STROKE_WIDTH_PX * 2),
                )
            }
        }
    }
}

private const val DAYS_PER_ROW = 365
private const val BASE_CELL_DP = 4f
private const val BASE_GAP_DP = 1f
private const val MIN_SCALE = 0.5f
private const val MAX_SCALE = 4f
private const val MIN_PULSE_ALPHA = 0.3f
private const val PULSE_DURATION_MS = 900
private const val STROKE_WIDTH_PX = 0.5f
