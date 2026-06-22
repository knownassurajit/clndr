package com.knownassurajit.clndr_widget.feature.lifegrid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.knownassurajit.clndr_widget.core.datetime.CellState
import com.knownassurajit.clndr_widget.core.datetime.LifeGridCalculator

/**
 * Renders Weeks/Months/Years granularities. Cell counts (5_720 / 1_320 / 110) are
 * small enough for LazyVerticalGrid to virtualise without canvas tricks.
 */
@Composable
fun LowDensityGrid(
    packed: IntArray,
    totalCells: Int,
    currentIndex: Int,
    columns: Int,
    modifier: Modifier = Modifier,
) {
    val indices = remember(totalCells) { List(totalCells) { it } }
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
    ) {
        items(indices, key = { it }) { index ->
            val state = LifeGridCalculator.unpackState(packed, index)
            Cell(state = state, isCurrent = index == currentIndex)
        }
    }
}

@Composable
private fun Cell(state: CellState, isCurrent: Boolean) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outline = MaterialTheme.colorScheme.outline
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RectangleShape)
            .let { base ->
                when (state) {
                    CellState.PAST -> base.background(onSurface)
                    CellState.FUTURE -> base.border(0.5.dp, outline)
                    CellState.PRESENT -> base.background(primary)
                }
            }
            .let { if (isCurrent) it.border(1.dp, primary) else it },
    ) {
        Box(Modifier.fillMaxSize())
    }
}
