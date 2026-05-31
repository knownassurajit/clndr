package com.clndr.feature.lifegrid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clndr.feature.lifegrid.components.ProgressLine
import com.clndr.feature.lifegrid.state.YearProgressViewModel
import java.time.LocalDate

@Composable
fun YearProgressScreen(
    birthDate: LocalDate?,
    modifier: Modifier = Modifier,
    viewModel: YearProgressViewModel = hiltViewModel(),
) {
    viewModel.bindBirthDate(birthDate)
    val buckets by viewModel.buckets.collectAsState()

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text("Now", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Elapsed and remaining across concurrent cycles.",
            style = MaterialTheme.typography.bodySmall,
        )
        ProgressLine("Era (110 yrs)", buckets.eraPct)
        ProgressLine("Decade", buckets.decadePct)
        ProgressLine("Year", buckets.yearPct)
        ProgressLine("Month", buckets.monthPct)
        ProgressLine("Week", buckets.weekPct)
        ProgressLine("Day", buckets.dayPct)
    }
}
