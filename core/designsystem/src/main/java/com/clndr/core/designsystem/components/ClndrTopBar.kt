package com.clndr.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clndr.core.designsystem.theme.ClndrText
import com.clndr.core.designsystem.theme.clndr

/**
 * Flat top bar: an info button on the left, a centered title (with an optional all-caps
 * sub-label), and a settings button on the right. Transparent so it sits on the screen tier.
 */
@Composable
fun ClndrTopBar(
    title: String,
    onInfo: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    sub: String? = null,
) {
    val palette = MaterialTheme.clndr
    Box(
        modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 8.dp),
    ) {
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                color = palette.txtHi,
            )
            if (sub != null) {
                Text(
                    sub.uppercase(),
                    style = ClndrText.eyebrow,
                    color = palette.txtLow,
                )
            }
        }
        androidx.compose.foundation.layout.Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onInfo) {
                Icon(Icons.Outlined.Info, contentDescription = "About", tint = palette.txtMid)
            }
            IconButton(onClick = onSettings) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = palette.txtMid)
            }
        }
    }
}
