package com.knownassurajit.clndr_widget.feature.lifegrid.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.knownassurajit.clndr_widget.core.datetime.CellState
import com.knownassurajit.clndr_widget.core.datetime.Granularity
import com.knownassurajit.clndr_widget.core.datetime.LifeGridCalculator
import com.knownassurajit.clndr_widget.core.designsystem.theme.ClndrPalette
import com.knownassurajit.clndr_widget.core.designsystem.theme.clndr
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

/**
 * Unified life-grid renderer for every granularity. Days fit columns to width (~5.2dp cells);
 * weeks/months/years use the fixed 52/12/10 column counts from the web design. Rendering is a
 * virtualized [LazyColumn] of per-row [Canvas]es so even the ~40k-cell days view stays cheap,
 * and the present cell scrolls into view as a bright hollow ring.
 */
@Composable
fun LifeGridCanvas(
    packed: IntArray,
    totalCells: Int,
    currentIndex: Int,
    gran: Granularity,
    modifier: Modifier = Modifier,
) {
    if (totalCells <= 0) return
    val palette = MaterialTheme.clndr
    val density = LocalDensity.current

    BoxWithConstraints(modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val gap = gapFor(gran)
        val gapPx = with(density) { gap.toPx() }
        val baseCellPx = with(density) { 5.2.dp.toPx() }

        val cols = remember(gran, widthPx, gapPx) {
            when (gran) {
                Granularity.DAYS -> max(1, floor((widthPx + gapPx) / (baseCellPx + gapPx)).toInt())
                Granularity.WEEKS -> 52
                Granularity.MONTHS -> 12
                Granularity.YEARS -> 10
            }
        }
        val cellPx = (widthPx - (cols - 1) * gapPx) / cols
        val cellDp = with(density) { cellPx.toDp() }
        val radiusPx = with(density) { radiusFor(gran).toPx() }.coerceAtMost(cellPx / 2f)
        val strokePx = with(density) { 1.4.dp.toPx() }
        val rows = ceil(totalCells.toDouble() / cols).toInt()
        val todayRow = if (currentIndex in 0 until totalCells) currentIndex / cols else -1

        val listState = rememberLazyListState()
        LaunchedEffect(gran, todayRow, cols) {
            if (todayRow >= 0) listState.scrollToItem(max(0, todayRow - SCROLL_LEAD_ROWS))
        }

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            items(rows) { row ->
                RowCells(
                    row = row,
                    cols = cols,
                    total = totalCells,
                    currentIndex = currentIndex,
                    packed = packed,
                    cellPx = cellPx,
                    gapPx = gapPx,
                    radiusPx = radiusPx,
                    strokePx = strokePx,
                    cellHeight = cellDp,
                    palette = palette,
                )
            }
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun RowCells(
    row: Int,
    cols: Int,
    total: Int,
    currentIndex: Int,
    packed: IntArray,
    cellPx: Float,
    gapPx: Float,
    radiusPx: Float,
    strokePx: Float,
    cellHeight: Dp,
    palette: ClndrPalette,
) {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(cellHeight),
    ) {
        val corner = CornerRadius(radiusPx, radiusPx)
        val cellSize = Size(cellPx, cellPx)
        for (col in 0 until cols) {
            val index = row * cols + col
            if (index >= total) break
            val topLeft = Offset(col * (cellPx + gapPx), 0f)
            when (LifeGridCalculator.unpackState(packed, index)) {
                CellState.PAST -> drawRoundRect(
                    color = palette.nodeLived,
                    topLeft = topLeft,
                    size = cellSize,
                    cornerRadius = corner,
                )
                CellState.FUTURE -> drawRoundRect(
                    color = palette.nodeFuture,
                    topLeft = topLeft,
                    size = cellSize,
                    cornerRadius = corner,
                    style = Stroke(width = strokePx),
                )
                CellState.PRESENT -> drawRoundRect(
                    color = palette.today,
                    topLeft = topLeft,
                    size = cellSize,
                    cornerRadius = corner,
                    style = Stroke(width = max(strokePx, cellPx * PRESENT_RING_FRACTION)),
                )
            }
            if (index == currentIndex) {
                drawRoundRect(
                    color = palette.today,
                    topLeft = topLeft,
                    size = cellSize,
                    cornerRadius = corner,
                    style = Stroke(width = max(strokePx, cellPx * PRESENT_RING_FRACTION)),
                )
            }
        }
    }
}

private fun gapFor(gran: Granularity): Dp = when (gran) {
    Granularity.DAYS -> 1.7.dp
    Granularity.WEEKS -> 2.4.dp
    Granularity.MONTHS -> 5.dp
    Granularity.YEARS -> 6.dp
}

private fun radiusFor(gran: Granularity): Dp = when (gran) {
    Granularity.DAYS -> 1.5.dp
    Granularity.WEEKS -> 1.5.dp
    Granularity.MONTHS -> 6.dp
    Granularity.YEARS -> 9.dp
}

private const val SCROLL_LEAD_ROWS = 6
private const val PRESENT_RING_FRACTION = 0.18f
