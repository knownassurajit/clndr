package com.clndr.feature.lifegrid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clndr.core.datetime.Granularity
import com.clndr.feature.lifegrid.components.DaysCanvasGrid
import com.clndr.feature.lifegrid.components.LowDensityGrid
import com.clndr.feature.lifegrid.state.LifeGridIntent
import com.clndr.feature.lifegrid.state.LifeGridViewModel
import java.time.LocalDate

@Composable
fun LifeGridScreen(
    birthDate: LocalDate?,
    modifier: Modifier = Modifier,
    viewModel: LifeGridViewModel = hiltViewModel(),
) {
    viewModel.bindBirthDate(birthDate)
    val state by viewModel.state.collectAsState()

    Column(modifier.fillMaxSize().padding(8.dp)) {
        GranularityRow(
            selected = state.granularity,
            onSelect = { viewModel.onIntent(LifeGridIntent.SetGranularity(it)) },
        )
        Box(Modifier.fillMaxSize().padding(top = 8.dp)) {
            when {
                state.birthDate == null -> EmptyHint()
                state.granularity == Granularity.DAYS -> DaysCanvasGrid(
                    packed = state.packedStates,
                    currentIndex = state.currentIndex,
                    totalCells = state.totalCells,
                )
                else -> LowDensityGrid(
                    packed = state.packedStates,
                    totalCells = state.totalCells,
                    currentIndex = state.currentIndex,
                    columns = columnsFor(state.granularity),
                )
            }
        }
    }
}

@Composable
private fun GranularityRow(
    selected: Granularity,
    onSelect: (Granularity) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Granularity.entries.forEach { g ->
            FilterChip(
                selected = g == selected,
                onClick = { onSelect(g) },
                label = { Text(g.name) },
            )
        }
    }
}

@Composable
private fun EmptyHint() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "Set your date of birth in Settings to render the grid.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

private fun columnsFor(g: Granularity): Int = when (g) {
    Granularity.DAYS -> WEEKS_GRID_COLS // unused — Days uses Canvas path
    Granularity.WEEKS -> WEEKS_GRID_COLS
    Granularity.MONTHS -> MONTHS_GRID_COLS
    Granularity.YEARS -> YEARS_GRID_COLS
}

private const val WEEKS_GRID_COLS = 52
private const val MONTHS_GRID_COLS = 12
private const val YEARS_GRID_COLS = 10
