package com.clndr.feature.lifegrid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clndr.core.designsystem.components.ClndrCard
import com.clndr.core.designsystem.components.ClndrProgressLine
import com.clndr.core.designsystem.components.Eyebrow
import com.clndr.core.designsystem.theme.ClndrText
import com.clndr.core.designsystem.theme.clndr
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.Locale

private val MONTH_LETTERS = listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
private const val MAX_DAY_ROWS = 31

private enum class DayState { PAST, TODAY, FUTURE, EMPTY }

@Composable
fun YearCalendarScreen(
    today: LocalDate = LocalDate.now(),
    modifier: Modifier = Modifier,
) {
    val palette = MaterialTheme.clndr
    val year = today.year
    val leap = Year.isLeap(year.toLong())
    val totalDays = if (leap) 366 else 365
    val elapsed = today.dayOfYear
    val remaining = totalDays - elapsed
    val pct = elapsed.toDouble() / totalDays * 100.0

    // monthLengths[m] = number of days in month m (0-based index)
    val monthLengths = remember(year) { IntArray(12) { YearMonth.of(year, it + 1).lengthOfMonth() } }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 4.dp, bottom = 28.dp),
    ) {
        ClndrCard {
            Column {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column {
                        Eyebrow("The year")
                        Text(
                            year.toString(),
                            style = ClndrText.numLarge,
                            color = palette.txtHi,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(elapsed.toString(), style = ClndrText.numMedium, color = palette.txtHi)
                        Text("of $totalDays days", style = MaterialTheme.typography.labelSmall, color = palette.txtLow)
                        Text(
                            if (leap) "leap year" else "common year",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.txtLow,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                ClndrProgressLine(fraction = (pct / 100.0).toFloat())
                Spacer(Modifier.height(9.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${format1(pct)}% spent", style = MaterialTheme.typography.bodySmall, color = palette.txtMid)
                    Text(
                        "$remaining days remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.txtMid
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Month-letter header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            MONTH_LETTERS.forEach { letter ->
                Text(
                    letter,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.txtLow,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(7.dp))

        // 31 rows × 12 month columns of day bars
        Column(verticalArrangement = Arrangement.spacedBy(2.4.dp)) {
            for (d in 1..MAX_DAY_ROWS) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (m in 0 until 12) {
                        val state = when {
                            d > monthLengths[m] -> DayState.EMPTY
                            else -> {
                                val di = LocalDate.of(year, m + 1, d).dayOfYear
                                when {
                                    di < elapsed -> DayState.PAST
                                    di == elapsed -> DayState.TODAY
                                    else -> DayState.FUTURE
                                }
                            }
                        }
                        YrCell(state)
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        YearLegend()
    }
}

@Composable
private fun RowScope.YrCell(state: DayState) {
    val palette = MaterialTheme.clndr
    val shape = RoundedCornerShape(2.dp)
    val base = Modifier
        .weight(1f)
        .height(6.dp)
    when (state) {
        DayState.EMPTY -> Box(base)
        DayState.PAST -> Box(base.clip(shape).background(palette.nodePast))
        DayState.TODAY -> Box(base.clip(shape).background(palette.today))
        DayState.FUTURE -> Box(base.clip(shape).border(1.3.dp, palette.nodeFuture, shape))
    }
}

@Composable
private fun YearLegend() {
    val palette = MaterialTheme.clndr
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        LegendSwatch("Past", palette.nodePast, filled = true)
        Spacer(Modifier.width(18.dp))
        LegendSwatch("Remaining", palette.nodeFuture, filled = false)
        Spacer(Modifier.width(18.dp))
        LegendSwatch("Today", palette.today, filled = true)
    }
}

@Composable
private fun LegendSwatch(label: String, color: Color, filled: Boolean) {
    val palette = MaterialTheme.clndr
    val shape = RoundedCornerShape(2.5.dp)
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (filled) {
            Box(Modifier.size(11.dp).clip(shape).background(color))
        } else {
            Box(Modifier.size(11.dp).clip(shape).border(1.4.dp, color, shape))
        }
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = palette.txtLow)
    }
}

private fun format1(n: Double): String = String.format(Locale.US, "%.1f", n)
