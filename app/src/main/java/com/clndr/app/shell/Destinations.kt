package com.clndr.app.shell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    data object Life : Destination("life", "Life", Icons.Outlined.GridOn)
    data object Year : Destination("year", "Year", Icons.Outlined.CalendarMonth)
    data object Progress : Destination("progress", "Now", Icons.Outlined.Timeline)
    data object Milestones : Destination("milestones", "Goals", Icons.Outlined.Flag)

    companion object {
        val all = listOf(Life, Year, Progress, Milestones)
    }
}
