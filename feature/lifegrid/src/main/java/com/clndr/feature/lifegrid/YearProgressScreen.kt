package com.clndr.feature.lifegrid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clndr.core.designsystem.components.ClndrCard
import com.clndr.core.designsystem.components.ClndrProgressLine
import com.clndr.core.designsystem.components.Eyebrow
import com.clndr.core.designsystem.theme.ClndrText
import com.clndr.core.designsystem.theme.clndr
import com.clndr.feature.lifegrid.state.YearProgressViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val MMM_D = DateTimeFormatter.ofPattern("MMM d", Locale.US)
private val MMM_D_Y = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
private val MONTH_FULL = DateTimeFormatter.ofPattern("MMMM", Locale.US)

@Composable
fun YearProgressScreen(
    modifier: Modifier = Modifier,
    viewModel: YearProgressViewModel = hiltViewModel(),
) {
    val buckets by viewModel.buckets.collectAsState()
    val palette = MaterialTheme.clndr

    val today = LocalDate.now()
    val year = today.year
    val weekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())

    val cycles = listOf(
        Cycle("Decade", "${(year / 10) * 10}s", buckets.decadePct),
        Cycle("Month", today.format(MONTH_FULL), buckets.monthPct),
        Cycle("Week", weekStart.format(MMM_D), buckets.weekPct),
        Cycle("Day", today.format(MMM_D), buckets.dayPct),
    )

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 4.dp, bottom = 28.dp),
    ) {
        HeroCard(year = year, yearPct = buckets.yearPct)

        Spacer(Modifier.height(24.dp))
        Eyebrow("Concurrent cycles", modifier = Modifier.padding(start = 2.dp, bottom = 4.dp))

        ClndrCard(padding = 0.dp) {
            Column(Modifier.padding(horizontal = 18.dp)) {
                cycles.forEachIndexed { index, cycle ->
                    CycleRow(cycle)
                    if (index != cycles.lastIndex) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.line))
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            "Updating live · ${today.format(MMM_D_Y)}",
            style = MaterialTheme.typography.labelSmall,
            color = palette.txtFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private data class Cycle(val label: String, val sub: String, val pct: Double)

@Composable
private fun HeroCard(year: Int, yearPct: Double) {
    val palette = MaterialTheme.clndr
    val spent = yearPct * 100.0
    val spentStr = format2(spent)
    val parts = spentStr.split(".")
    val intPart = parts[0]
    val frac = parts.getOrElse(1) { "00" }

    ClndrCard(padding = 20.dp) {
        Column {
            Eyebrow("$year in progress")
            Row(
                Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(intPart, style = ClndrText.numHero, color = palette.txtHi)
                Text(".$frac", style = ClndrText.numHero.copy(fontSize = 38.sp), color = palette.txtMid)
                Text(
                    "%",
                    style = ClndrText.numHero.copy(fontSize = 28.sp),
                    color = palette.txtLow,
                    modifier = Modifier.padding(start = 2.dp, bottom = 6.dp),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "ELAPSED",
                    style = ClndrText.eyebrow,
                    color = palette.txtLow,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            ClndrProgressLine(fraction = yearPct.toFloat())
            Spacer(Modifier.height(11.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${format2(spent)}% spent", style = MaterialTheme.typography.bodyMedium, color = palette.txtHi)
                Text("${format2(100.0 - spent)}% left", style = MaterialTheme.typography.bodyMedium, color = palette.txtLow)
            }
        }
    }
}

@Composable
private fun CycleRow(cycle: Cycle) {
    val palette = MaterialTheme.clndr
    Row(
        Modifier.fillMaxWidth().padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.width(86.dp)) {
            Text(cycle.label, style = MaterialTheme.typography.titleMedium, color = palette.txtHi)
            Text(cycle.sub, style = MaterialTheme.typography.bodySmall, color = palette.txtLow)
        }
        Box(Modifier.weight(1f).padding(horizontal = 8.dp)) {
            ClndrProgressLine(fraction = cycle.pct.toFloat())
        }
        Text(
            "${format1(cycle.pct * 100.0)}%",
            style = ClndrText.numSmall,
            color = palette.txtHi,
            textAlign = TextAlign.End,
            modifier = Modifier.width(52.dp),
        )
    }
}

private fun format1(n: Double): String = String.format(Locale.US, "%.1f", n)
private fun format2(n: Double): String = String.format(Locale.US, "%.2f", n)
