package com.clndr.feature.milestones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clndr.feature.milestones.model.MilestoneUi
import com.clndr.feature.milestones.state.MilestonesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestonesListScreen(
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MilestonesListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Outlined.Add, contentDescription = "Add milestone")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (state.upcoming.isNotEmpty()) {
                item { SectionHeader("Upcoming") }
                items(state.upcoming, key = { "u-${it.id}" }) { item -> MilestoneRow(item, onEdit) }
            }
            if (state.past.isNotEmpty()) {
                item { SectionHeader("Past") }
                items(state.past, key = { "p-${it.id}" }) { item -> MilestoneRow(item, onEdit) }
            }
            if (state.upcoming.isEmpty() && state.past.isEmpty()) {
                item {
                    Text(
                        "No milestones yet. Tap + to anchor your first one.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(label: String) {
    Text(label, style = MaterialTheme.typography.titleSmall)
}

@Composable
private fun MilestoneRow(item: MilestoneUi, onEdit: (Long) -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onEdit(item.id) },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text(item.targetDate.toString(), style = MaterialTheme.typography.bodySmall)
                item.description?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
            Text(item.countdownLabel, style = MaterialTheme.typography.labelLarge)
        }
    }
}
