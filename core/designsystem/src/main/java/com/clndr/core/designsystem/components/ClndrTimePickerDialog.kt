package com.clndr.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime

/**
 * A thin wrapper over the Material3 time picker that speaks `java.time.LocalTime`.
 * Used for the milestone reminder/calendar time. Mirrors [ClndrDatePickerDialog]'s
 * unit-test fallback so Robolectric layouts don't choke on the clock face.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClndrTimePickerDialog(
    initialTime: LocalTime?,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
) {
    if (isRunningUnitTest()) {
        TextButton(onClick = {
            onConfirm(initialTime ?: LocalTime.of(9, 0))
            onDismiss()
        }) {
            Text("Set time")
        }
        return
    }

    val start = initialTime ?: LocalTime.of(9, 0)
    val state = rememberTimePickerState(
        initialHour = start.hour,
        initialMinute = start.minute,
        is24Hour = false,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(LocalTime.of(state.hour, state.minute))
                onDismiss()
            }) { Text("Set") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            Box(Modifier.padding(top = 8.dp)) {
                TimePicker(state = state)
            }
        },
    )
}

private fun isRunningUnitTest(): Boolean {
    return Thread.currentThread().stackTrace.any {
        it.className.contains("robolectric", ignoreCase = true) ||
        it.className.contains("junit", ignoreCase = true)
    }
}
