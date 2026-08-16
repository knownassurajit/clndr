package com.knownassurajit.clndr_widget.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.knownassurajit.clndr_widget.core.designsystem.theme.clndr

data class ClndrNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

/**
 * Bottom navigation matching the M3-expressive web design: an active item gets a filled
 * pill behind its icon and high-contrast text; inactive items are muted. The whole bar
 * lifts above the system gesture inset.
 */
@Composable
fun ClndrNavBar(
    items: List<ClndrNavItem>,
    currentRoute: String?,
    onSelect: (ClndrNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = MaterialTheme.clndr
    Column(modifier.fillMaxWidth().background(palette.screen)) {
        HorizontalDivider(thickness = 1.dp, color = palette.line)
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                NavItem(
                    item = item,
                    active = currentRoute == item.route,
                    onClick = { onSelect(item) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    item: ClndrNavItem,
    active: Boolean,
    onClick: () -> Unit,
) {
    val palette = MaterialTheme.clndr
    val tint = if (active) palette.txtHi else palette.txtLow
    Column(
        Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            Modifier
                .width(56.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (active) palette.surface2 else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                item.icon,
                contentDescription = item.label,
                tint = tint,
                modifier = Modifier.height(21.dp).width(21.dp),
            )
        }
        Text(
            item.label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}
