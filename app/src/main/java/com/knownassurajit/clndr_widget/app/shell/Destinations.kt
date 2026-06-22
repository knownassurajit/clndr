package com.knownassurajit.clndr_widget.app.shell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    data object Life : Destination("life", "Life", Icons.Outlined.GridView)
    data object Year : Destination("year", "Year", Icons.Outlined.CalendarMonth)
    data object Progress : Destination("progress", "Progress", Icons.Outlined.Schedule)
    data object Milestones : Destination("milestones", "Goals", Icons.Outlined.Flag)

    companion object {
        val all = listOf(Life, Year, Progress, Milestones)
    }
}
