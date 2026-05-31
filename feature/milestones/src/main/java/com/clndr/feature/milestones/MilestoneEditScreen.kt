package com.clndr.feature.milestones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clndr.feature.milestones.state.MilestoneEditEffect
import com.clndr.feature.milestones.state.MilestoneEditViewModel

@Composable
fun MilestoneEditScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MilestoneEditViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MilestoneEditEffect.NavigateBack -> onDone()
                is MilestoneEditEffect.RequestExactAlarmPermission -> {
                    // Handled by the host (MainActivity) — surfaced via shared effects bus.
                }
            }
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            if (state.draft.id == 0L) "New milestone" else "Edit milestone",
            style = MaterialTheme.typography.headlineSmall,
        )

        OutlinedTextField(
            value = state.draft.title,
            onValueChange = { v -> viewModel.update { it.copy(title = v) } },
            label = { Text("Title") },
            isError = state.errors.containsKey("title"),
            supportingText = { state.errors["title"]?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.draft.description,
            onValueChange = { v -> viewModel.update { it.copy(description = v) } },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
        )

        OutlinedTextField(
            value = state.draft.targetDate.toString(),
            onValueChange = { v ->
                runCatching { java.time.LocalDate.parse(v) }
                    .onSuccess { parsed -> viewModel.update { it.copy(targetDate = parsed) } }
            },
            label = { Text("Target date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Reminder", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = state.draft.reminderEnabled,
                onCheckedChange = { v -> viewModel.update { it.copy(reminderEnabled = v) } },
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Add to system calendar", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = state.draft.mirrorToCalendar,
                onCheckedChange = { v -> viewModel.update { it.copy(mirrorToCalendar = v) } },
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (state.draft.id != 0L) {
                OutlinedButton(
                    onClick = { viewModel.delete() },
                    modifier = Modifier.weight(1f),
                ) { Text("Delete") }
            }
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.weight(1f),
                enabled = !state.isSaving,
            ) { Text(if (state.isSaving) "Saving…" else "Save") }
        }
    }
}
