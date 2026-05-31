package com.clndr.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clndr.core.designsystem.theme.ThemeMode
import java.time.LocalDate

data class ClndrSettingsState(
    val birthDate: LocalDate?,
    val themeMode: ThemeMode,
    val sunriseAutoEnabled: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClndrSettingsSheet(
    state: ClndrSettingsState,
    onDismiss: () -> Unit,
    onEditBirthDate: () -> Unit,
    onToggleSunriseAuto: (Boolean) -> Unit,
    onCycleThemeMode: () -> Unit,
    onPinWidget: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(horizontal = 24.dp, vertical = 16.dp)),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineSmall)

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Date of birth", style = MaterialTheme.typography.titleMedium)
                    Text(
                        state.birthDate?.toString() ?: "Not set",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                OutlinedButton(onClick = onEditBirthDate) { Text("Edit") }
            }

            Divider()

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Theme", style = MaterialTheme.typography.titleMedium)
                    Text(state.themeMode.name, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedButton(onClick = onCycleThemeMode) { Text("Cycle") }
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Sunrise auto", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Switch theme at local sunrise / sunset.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Switch(checked = state.sunriseAutoEnabled, onCheckedChange = onToggleSunriseAuto)
            }

            Divider()

            OutlinedButton(onClick = onPinWidget, modifier = Modifier.fillMaxWidth()) {
                Text("Add Year Progress widget to Home")
            }
        }
    }
}
