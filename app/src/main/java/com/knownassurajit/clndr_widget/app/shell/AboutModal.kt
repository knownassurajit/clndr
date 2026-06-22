package com.knownassurajit.clndr_widget.app.shell

import androidx.compose.runtime.Composable
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrInfoSheet

@Composable
fun AboutModal(onDismiss: () -> Unit) {
    ClndrInfoSheet(onDismiss = onDismiss)
}
