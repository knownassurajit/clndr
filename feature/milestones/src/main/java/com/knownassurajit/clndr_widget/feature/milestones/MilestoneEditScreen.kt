package com.knownassurajit.clndr_widget.feature.milestones

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrDatePickerDialog
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrTimePickerDialog
import com.knownassurajit.clndr_widget.core.designsystem.theme.clndr
import com.knownassurajit.clndr_widget.feature.milestones.state.MilestoneEditEffect
import com.knownassurajit.clndr_widget.feature.milestones.state.MilestoneEditViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
private val TIME_FMT = DateTimeFormatter.ofPattern("h:mm a", Locale.US)

sealed interface MilestoneEditAction {
    data class TitleChanged(val title: String) : MilestoneEditAction
    data class DescriptionChanged(val description: String) : MilestoneEditAction
    object PickDate : MilestoneEditAction
    object PickTime : MilestoneEditAction
    object ClearTime : MilestoneEditAction
    data class ReminderToggled(val checked: Boolean) : MilestoneEditAction
    data class CalendarToggled(val checked: Boolean) : MilestoneEditAction
    object Delete : MilestoneEditAction
    object Save : MilestoneEditAction
}

@Composable
fun MilestoneEditScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MilestoneEditViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val isNew = state.draft.id == 0L
    var showPicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                viewModel.update { it.copy(mirrorToCalendar = false) }
            }
        }
    )

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                viewModel.update { it.copy(reminderEnabled = false) }
            }
        }
    )

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MilestoneEditEffect.NavigateBack -> onDone()
                is MilestoneEditEffect.RequestExactAlarmPermission -> {
                    runCatching {
                        val intent = Intent(effect.deepLinkAction).apply {
                            data = Uri.parse("package:${context.packageName}")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    MilestoneEditContent(
        state = state,
        isNew = isNew,
        onAction = { action ->
            handleEditAction(
                action = action,
                viewModel = viewModel,
                context = context,
                calendarLauncher = calendarPermissionLauncher,
                notificationLauncher = notificationPermissionLauncher,
                onShowPicker = { showPicker = true },
                onShowTimePicker = { showTimePicker = true },
            )
        },
        modifier = modifier,
    )

    if (showPicker) {
        ClndrDatePickerDialog(
            initialDate = state.draft.targetDate,
            onDismiss = { showPicker = false },
            onConfirm = { picked -> viewModel.update { it.copy(targetDate = picked) } },
        )
    }

    if (showTimePicker) {
        ClndrTimePickerDialog(
            initialTime = state.draft.targetTime,
            onDismiss = { showTimePicker = false },
            onConfirm = { picked -> viewModel.update { it.copy(targetTime = picked) } },
        )
    }
}

private fun handleEditAction(
    action: MilestoneEditAction,
    viewModel: MilestoneEditViewModel,
    context: android.content.Context,
    calendarLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    notificationLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    onShowPicker: () -> Unit,
    onShowTimePicker: () -> Unit,
) {
    when (action) {
        is MilestoneEditAction.TitleChanged -> viewModel.update { it.copy(title = action.title) }
        is MilestoneEditAction.DescriptionChanged -> viewModel.update { it.copy(description = action.description) }
        MilestoneEditAction.PickDate -> onShowPicker()
        MilestoneEditAction.PickTime -> onShowTimePicker()
        MilestoneEditAction.ClearTime -> viewModel.update { it.copy(targetTime = null) }
        is MilestoneEditAction.ReminderToggled -> {
            val checked = action.checked
            val needPermission = checked &&
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            if (needPermission) {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            viewModel.update { it.copy(reminderEnabled = checked) }
        }
        is MilestoneEditAction.CalendarToggled -> {
            val checked = action.checked
            val needPermission = checked &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
            if (needPermission) {
                calendarLauncher.launch(Manifest.permission.WRITE_CALENDAR)
            }
            viewModel.update { it.copy(mirrorToCalendar = checked) }
        }
        MilestoneEditAction.Delete -> viewModel.delete()
        MilestoneEditAction.Save -> viewModel.save()
    }
}

@Composable
private fun MilestoneEditContent(
    state: com.knownassurajit.clndr_widget.feature.milestones.state.MilestoneEditState,
    isNew: Boolean,
    onAction: (MilestoneEditAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = MaterialTheme.clndr
    Column(
        modifier
            .fillMaxSize()
            .background(palette.screen)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 8.dp, bottom = 28.dp),
    ) {
        Text(
            if (isNew) "New milestone" else "Edit milestone",
            style = MaterialTheme.typography.headlineSmall,
            color = palette.txtHi,
        )
        Text(
            "Anchor a moment. Past or future — clndr never deletes it.",
            style = MaterialTheme.typography.bodySmall,
            color = palette.txtLow,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        )

        FieldLabel("What is it?")
        OutlinedTextField(
            value = state.draft.title,
            onValueChange = { v -> onAction(MilestoneEditAction.TitleChanged(v)) },
            placeholder = { Text("Goal: finish refactoring") },
            singleLine = true,
            isError = state.errors.containsKey("title"),
            supportingText = { state.errors["title"]?.let { Text(it) } },
            colors = fieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))
        FieldLabel("Notes")
        OutlinedTextField(
            value = state.draft.description,
            onValueChange = { v -> onAction(MilestoneEditAction.DescriptionChanged(v)) },
            minLines = 2,
            colors = fieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))
        FieldLabel("Date")
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface)
                .border(1.dp, palette.line, RoundedCornerShape(12.dp))
                .clickable { onAction(MilestoneEditAction.PickDate) }
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            Text(
                state.draft.targetDate.format(DATE_FMT),
                style = MaterialTheme.typography.bodyLarge,
                color = palette.txtHi
            )
        }

        Spacer(Modifier.height(16.dp))
        FieldLabel("Time")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.line, RoundedCornerShape(12.dp))
                    .clickable { onAction(MilestoneEditAction.PickTime) }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                Text(
                    state.draft.targetTime?.format(TIME_FMT) ?: "All-day (9:00 AM reminder)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (state.draft.targetTime != null) palette.txtHi else palette.txtLow,
                )
            }
            if (state.draft.targetTime != null) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, palette.line, RoundedCornerShape(12.dp))
                        .clickable { onAction(MilestoneEditAction.ClearTime) }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Clear", style = MaterialTheme.typography.labelLarge, color = palette.txtMid)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        ToggleRow(
            title = "Set a system reminder",
            sub = "Rings at the chosen time via the device",
            checked = state.draft.reminderEnabled,
            onChange = { checked -> onAction(MilestoneEditAction.ReminderToggled(checked)) },
        )
        ToggleRow(
            title = "Add to system calendar",
            sub = "Mirrors this milestone as an all-day event",
            checked = state.draft.mirrorToCalendar,
            onChange = { checked -> onAction(MilestoneEditAction.CalendarToggled(checked)) },
        )

        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!isNew) {
                OutlinedButton(onClick = { onAction(MilestoneEditAction.Delete) }, modifier = Modifier.weight(1f)) {
                    Text("Delete")
                }
            }
            Button(
                onClick = { onAction(MilestoneEditAction.Save) },
                enabled = !state.isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.nodeLived,
                    contentColor = palette.screen,
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(if (state.isSaving) "Saving…" else if (isNew) "Anchor milestone" else "Save changes")
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.clndr.txtMid,
        modifier = Modifier.padding(bottom = 7.dp),
    )
}

@Composable
private fun ToggleRow(title: String, sub: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val palette = MaterialTheme.clndr
    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = palette.txtHi)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = palette.txtLow)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.screen,
                checkedTrackColor = palette.nodeLived,
                uncheckedThumbColor = palette.screen,
                uncheckedTrackColor = palette.nodeFuture,
                uncheckedBorderColor = palette.nodeFuture,
            ),
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.clndr.txtHi,
    unfocusedTextColor = MaterialTheme.clndr.txtHi,
    focusedContainerColor = MaterialTheme.clndr.surface,
    unfocusedContainerColor = MaterialTheme.clndr.surface,
    focusedBorderColor = MaterialTheme.clndr.lineStrong,
    unfocusedBorderColor = MaterialTheme.clndr.line,
    cursorColor = MaterialTheme.clndr.txtHi,
)
