package com.knownassurajit.clndr_widget.feature.milestones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrCard
import com.knownassurajit.clndr_widget.core.designsystem.components.Eyebrow
import com.knownassurajit.clndr_widget.core.designsystem.components.SubHead
import com.knownassurajit.clndr_widget.core.designsystem.theme.ClndrText
import com.knownassurajit.clndr_widget.core.designsystem.theme.clndr
import com.knownassurajit.clndr_widget.feature.milestones.model.MilestoneUi
import com.knownassurajit.clndr_widget.feature.milestones.state.MilestonesListViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private val DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)

@Composable
fun MilestonesListScreen(
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MilestonesListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val today = state.today

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 4.dp),
    ) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Eyebrow("Milestones")
                Spacer(Modifier.weight(1f))
                AddButton(onAdd)
            }
        }

        if (state.upcoming.isNotEmpty()) {
            item { SubHead("Ahead", Modifier.padding(vertical = 6.dp)) }
            items(state.upcoming, key = { "u-${it.id}" }) { item ->
                MilestoneCard(item, today, onEdit = onEdit, onDelete = viewModel::delete)
            }
        }
        if (state.past.isNotEmpty()) {
            item { SubHead("Behind", Modifier.padding(top = 14.dp, bottom = 6.dp)) }
            items(state.past, key = { "p-${it.id}" }) { item ->
                MilestoneCard(item, today, onEdit = onEdit, onDelete = viewModel::delete)
            }
        }
        if (state.upcoming.isEmpty() && state.past.isEmpty()) {
            item { EmptyState() }
        }
    }
}

@Composable
private fun AddButton(onAdd: () -> Unit) {
    val palette = MaterialTheme.clndr
    Row(
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.nodeLived)
            .clickable(onClick = onAdd)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(Icons.Outlined.Add, contentDescription = null, tint = palette.screen, modifier = Modifier.size(16.dp))
        Text(
            "Add",
            style = MaterialTheme.typography.labelMedium,
            color = palette.screen,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MilestoneCard(
    item: MilestoneUi,
    today: LocalDate,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
) {
    val palette = MaterialTheme.clndr
    val days = item.targetDate.toEpochDay() - today.toEpochDay()
    val (badge, label) = when {
        days > 0 -> "Countdown" to "days remaining"
        days == 0L -> "Today" to "today"
        else -> "Count-up" to "days since"
    }
    Box(
        Modifier.fillMaxWidth().padding(bottom = 12.dp).clip(MaterialTheme.shapes.medium).clickable { onEdit(item.id) }
    ) {
        ClndrCard {
            Column {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Eyebrow(badge)
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable { onDelete(item.id) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete",
                            tint = palette.txtMid,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.txtHi,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    item.targetDate.format(DATE_FMT),
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.txtLow,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    Modifier.fillMaxWidth().padding(top = 14.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    Text(formatInt(abs(days)), style = ClndrText.numLarge, color = palette.txtHi)
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.txtMid,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    val palette = MaterialTheme.clndr
    Column(
        Modifier.fillMaxWidth().padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Outlined.Flag, contentDescription = null, tint = palette.txtLow, modifier = Modifier.size(34.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            "No milestones yet.\nAnchor one to start counting.",
            style = MaterialTheme.typography.bodyMedium,
            color = palette.txtLow,
            textAlign = TextAlign.Center,
        )
    }
}

private fun formatInt(n: Long): String = String.format(Locale.US, "%,d", n)
