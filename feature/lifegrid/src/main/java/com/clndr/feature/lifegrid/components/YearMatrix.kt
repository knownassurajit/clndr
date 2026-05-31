package com.clndr.feature.lifegrid.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.min

/**
 * Single Canvas that paints the 12 months of [year] as a 4×3 grid of mini-calendars.
 * Past days are filled, today is highlighted with a fill+stroke, future days are outlines.
 */
@Composable
fun YearMatrix(
    year: Int,
    today: LocalDate,
    modifier: Modifier = Modifier,
) {
    val pastColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline
    val presentColor = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.surface

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(background),
    ) {
        val cols = MONTH_COLS
        val rows = MONTH_ROWS
        val padding = PANEL_PADDING_PX
        val panelW = (size.width - padding * (cols + 1)) / cols
        val panelH = (size.height - padding * (rows + 1)) / rows

        for (m in 0 until MONTHS_PER_YEAR) {
            val r = m / cols
            val c = m % cols
            val originX = padding + c * (panelW + padding)
            val originY = padding + r * (panelH + padding)
            drawMonth(
                month = YearMonth.of(year, m + 1),
                today = today,
                originX = originX,
                originY = originY,
                width = panelW,
                height = panelH,
                pastColor = pastColor,
                outlineColor = outlineColor,
                presentColor = presentColor,
            )
        }
    }
}

@Suppress("LongParameterList")
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMonth(
    month: YearMonth,
    today: LocalDate,
    originX: Float,
    originY: Float,
    width: Float,
    height: Float,
    pastColor: Color,
    outlineColor: Color,
    presentColor: Color,
) {
    val cols = WEEK_COLS
    val rows = WEEK_ROWS
    val gap = CELL_GAP_PX
    val cellW = (width - gap * (cols - 1)) / cols
    val cellH = (height - gap * (rows - 1)) / rows
    val cellSize = min(cellW, cellH)
    val firstDayOffset = (month.atDay(1).dayOfWeek.value - 1) % cols

    for (day in 1..month.lengthOfMonth()) {
        val idx = firstDayOffset + (day - 1)
        val col = idx % cols
        val row = idx / cols
        if (row >= rows) break
        val x = originX + col * (cellSize + gap)
        val y = originY + row * (cellSize + gap)
        val date = month.atDay(day)
        val pos = Offset(x, y)
        val size = Size(cellSize, cellSize)
        when {
            date.isBefore(today) -> drawRect(color = pastColor, topLeft = pos, size = size)
            date == today -> {
                drawRect(color = presentColor, topLeft = pos, size = size)
                drawRect(
                    color = pastColor,
                    topLeft = pos,
                    size = size,
                    style = Stroke(width = STROKE_PX),
                )
            }
            else -> drawRect(
                color = outlineColor,
                topLeft = pos,
                size = size,
                style = Stroke(width = STROKE_PX),
            )
        }
    }
}

private const val MONTH_COLS = 4
private const val MONTH_ROWS = 3
private const val WEEK_COLS = 7
private const val WEEK_ROWS = 6
private const val MONTHS_PER_YEAR = 12
private const val CELL_GAP_PX = 2f
private const val PANEL_PADDING_PX = 8f
private const val STROKE_PX = 1f
