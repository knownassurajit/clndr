package com.clndr.feature.lifegrid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clndr.core.datetime.Granularity
import com.clndr.core.designsystem.components.ClndrCard
import com.clndr.core.designsystem.components.Eyebrow
import com.clndr.core.designsystem.components.SegmentedToggle
import com.clndr.core.designsystem.theme.ClndrText
import com.clndr.core.designsystem.theme.clndr
import com.clndr.feature.lifegrid.components.LifeGridCanvas
import com.clndr.feature.lifegrid.state.LifeGridIntent
import com.clndr.feature.lifegrid.state.LifeGridViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.max

@Composable
fun LifeGridScreen(
    birthDate: LocalDate?,
    modifier: Modifier = Modifier,
    viewModel: LifeGridViewModel = hiltViewModel(),
) {
    viewModel.bindBirthDate(birthDate)
    val state by viewModel.state.collectAsState()
    val palette = MaterialTheme.clndr

    if (birthDate == null) {
        EmptyHint(modifier)
        return
    }

    val today = LocalDate.now()
    val age = ChronoUnit.YEARS.between(birthDate, today).coerceAtLeast(0)

    // Only ever count time *lived* — no fixed lifespan, no "remaining".
    val livedUnits = max(0, state.currentIndex)
    val unitName = state.granularity.name.lowercase(Locale.US)

    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
            .padding(top = 4.dp, bottom = 8.dp),
    ) {
        ClndrCard {
            Column {
                Eyebrow("A life in $unitName")
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(formatInt(livedUnits.toLong()), style = ClndrText.numLarge.copy(fontSize = 52.sp), color = palette.txtHi)
                    Text(
                        " $unitName lived",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.txtMid,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                Spacer(Modifier.height(9.dp))
                Text(
                    "Age $age years",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.txtLow,
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        SegmentedToggle(
            options = Granularity.entries.map { it.name.lowercase(Locale.US).replaceFirstChar(Char::uppercase) },
            selectedIndex = Granularity.entries.indexOf(state.granularity),
            onSelect = { index ->
                viewModel.onIntent(LifeGridIntent.SetGranularity(Granularity.entries[index]))
            },
        )

        Spacer(Modifier.height(14.dp))

        Box(Modifier.fillMaxWidth().weight(1f)) {
            LifeGridCanvas(
                packed = state.packedStates,
                totalCells = state.totalCells,
                currentIndex = state.currentIndex,
                gran = state.granularity,
            )
        }

        Legend()
    }
}

@Composable
private fun Legend() {
    val palette = MaterialTheme.clndr
    Row(
        Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        LegendItem("Marked") {
            Box(Modifier.size(11.dp).clip(RoundedCornerShape(2.5.dp)).background(palette.nodeLived))
        }
        Spacer(Modifier.width(18.dp))
        LegendItem("Unspent") {
            Box(
                Modifier.size(
                    11.dp
                ).clip(RoundedCornerShape(2.5.dp)).border(1.4.dp, palette.nodeFuture, RoundedCornerShape(2.5.dp))
            )
        }
        Spacer(Modifier.width(18.dp))
        LegendItem("Now") {
            Box(
                Modifier.size(
                    11.dp
                ).clip(RoundedCornerShape(2.5.dp)).border(1.6.dp, palette.today, RoundedCornerShape(2.5.dp))
            )
        }
    }
}

@Composable
private fun LegendItem(label: String, swatch: @Composable () -> Unit) {
    val palette = MaterialTheme.clndr
    Row(verticalAlignment = Alignment.CenterVertically) {
        swatch()
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = palette.txtLow)
    }
}

@Composable
private fun EmptyHint(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            "Set your date of birth to render the grid.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.clndr.txtMid,
            textAlign = TextAlign.Center,
        )
    }
}

private fun formatInt(n: Long): String = String.format(Locale.US, "%,d", n)
