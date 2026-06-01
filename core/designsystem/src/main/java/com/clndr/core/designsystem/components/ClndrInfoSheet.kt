package com.clndr.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clndr.core.designsystem.theme.clndr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClndrInfoSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = MaterialTheme.clndr
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.screen,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 30.dp),
        ) {
            Eyebrow("clndr")
            Spacer(Modifier.height(10.dp))
            Text(
                "A calendar for the long view.",
                style = MaterialTheme.typography.headlineSmall,
                color = palette.txtHi,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Most calendars manage your next hour. clndr shows your next fifty years — " +
                    "the macro and the existential, rendered in flat monochrome so nothing competes " +
                    "with the only number that matters: time spent.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.txtMid,
            )
            Spacer(Modifier.height(16.dp))
            InfoRow("Perspective", "Macro & existential")
            InfoRow("Design", "Cinematic monochrome")
            InfoRow("Data", "Offline · on-device", divider = false)
        }
    }
}

@Composable
private fun InfoRow(main: String, sub: String, divider: Boolean = true) {
    val palette = MaterialTheme.clndr
    Row(
        Modifier.fillMaxWidth().padding(vertical = 15.dp),
    ) {
        Text(main, style = MaterialTheme.typography.titleMedium, color = palette.txtHi, modifier = Modifier.weight(1f))
        Text(sub, style = MaterialTheme.typography.bodySmall, color = palette.txtLow)
    }
    if (divider) Box(Modifier.fillMaxWidth().height(1.dp).background(palette.line))
}
