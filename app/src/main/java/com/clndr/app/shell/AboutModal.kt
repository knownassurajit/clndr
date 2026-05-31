package com.clndr.app.shell

import androidx.compose.runtime.Composable
import com.clndr.core.designsystem.components.ClndrInfoSheet

@Composable
fun AboutModal(onDismiss: () -> Unit) {
    ClndrInfoSheet(onDismiss = onDismiss)
}
