package com.knownassurajit.clndr_widget.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.knownassurajit.clndr_widget.core.designsystem.theme.clndr

private data class CreditLink(val label: String, val handle: String, val url: String, val icon: ImageVector)

private val DEV_LINKS = listOf(
    CreditLink("Portfolio", "surajitdas.vercel.app", "https://surajitdas.vercel.app", Icons.Outlined.Language),
    CreditLink("GitHub", "@knownassurajit", "https://github.com/knownassurajit", Icons.Outlined.Code),
    CreditLink("X", "@knownassurajit", "https://x.com/knownassurajit", Icons.Outlined.Link),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClndrInfoSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = MaterialTheme.clndr
    val uriHandler = LocalUriHandler.current
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
                "Most calendars manage your next hour. clndr shows your whole life — days, " +
                    "weeks, months and years already lived — rendered in flat monochrome so " +
                    "nothing competes with the only number that matters: time spent.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.txtMid,
            )

            SubHead("Purpose", Modifier.padding(top = 20.dp, bottom = 10.dp))
            InfoRow("Perspective", "See a life at a glance")
            InfoRow("Goals", "Anchor moments, set reminders")
            InfoRow("Data", "Offline · on-device", divider = false)

            SubHead("Built by", Modifier.padding(top = 22.dp, bottom = 12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(palette.surface2),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        tint = palette.txtHi,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.size(12.dp))
                Column {
                    Text("Surajit Das", style = MaterialTheme.typography.titleMedium, color = palette.txtHi)
                    Text("@knownassurajit", style = MaterialTheme.typography.bodySmall, color = palette.txtLow)
                }
            }

            Spacer(Modifier.height(14.dp))
            DEV_LINKS.forEach { link ->
                LinkRow(link) { uriHandler.openUri(link.url) }
            }
        }
    }
}

@Composable
private fun LinkRow(link: CreditLink, onClick: () -> Unit) {
    val palette = MaterialTheme.clndr
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, palette.line, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(link.icon, contentDescription = null, tint = palette.txtHi, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(link.label, style = MaterialTheme.typography.titleMedium, color = palette.txtHi)
            Text(link.handle, style = MaterialTheme.typography.bodySmall, color = palette.txtLow)
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = "Open",
            tint = palette.txtLow,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun InfoRow(main: String, sub: String, divider: Boolean = true) {
    val palette = MaterialTheme.clndr
    Row(
        Modifier.fillMaxWidth().padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(main, style = MaterialTheme.typography.titleMedium, color = palette.txtHi, modifier = Modifier.weight(1f))
        Text(sub, style = MaterialTheme.typography.bodySmall, color = palette.txtLow)
    }
    if (divider) Box(Modifier.fillMaxWidth().height(1.dp).background(palette.line))
}
