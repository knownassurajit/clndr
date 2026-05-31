package com.clndr.feature.lifegrid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clndr.feature.lifegrid.components.YearMatrix
import java.time.LocalDate

@Composable
fun YearCalendarScreen(
    today: LocalDate = LocalDate.now(),
    modifier: Modifier = Modifier,
) {
    val year = remember(today) { today.year }
    Column(modifier.fillMaxSize().padding(8.dp)) {
        Text("$year", style = MaterialTheme.typography.headlineMedium)
        Box(Modifier.fillMaxSize()) {
            YearMatrix(year = year, today = today)
        }
    }
}
