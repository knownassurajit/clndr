package com.knownassurajit.clndr_widget.feature.lifegrid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * One row of [label][elapsed%][bar][remaining%].
 * The bar is two stacked Boxes — no progress indicator so we keep strict monochrome.
 */
@Composable
fun ProgressLine(
    label: String,
    progress: Double,
    modifier: Modifier = Modifier,
) {
    val pct = progress.coerceIn(0.0, 1.0)
    Column(modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Text(
                String.format(java.util.Locale.US, "%.2f%% / %.2f%%", pct * 100.0, (1.0 - pct) * 100.0),
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(BAR_HEIGHT)
                .padding(top = 4.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(pct.toFloat())
                    .height(BAR_HEIGHT)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

private val BAR_HEIGHT = 6.dp
