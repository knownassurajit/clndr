package com.clndr.app.shell

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.clndr.app.widget.PinWidgetHelper
import com.clndr.core.designsystem.components.ClndrNavBar
import com.clndr.core.designsystem.components.ClndrNavItem
import com.clndr.core.designsystem.components.ClndrTopBar
import com.clndr.feature.lifegrid.LifeGridScreen
import com.clndr.feature.lifegrid.YearCalendarScreen
import com.clndr.feature.lifegrid.YearProgressScreen
import com.clndr.feature.milestones.MilestoneEditScreen
import com.clndr.feature.milestones.MilestonesListScreen
import com.clndr.feature.widgets.year.YearProgressWidgetReceiver
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClndrShell(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val settings by settingsViewModel.state.collectAsState()
    val ctx = LocalContext.current

    var showAbout by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val items = remember {
        Destination.all.map { ClndrNavItem(it.route, it.label, it.icon) }
    }

    Scaffold(
        topBar = {
            ClndrTopBar(
                title = currentTitle(currentRoute),
                onInfo = { showAbout = true },
                onSettings = { showSettings = true },
            )
        },
        bottomBar = {
            ClndrNavBar(
                items = items,
                currentRoute = currentRoute,
                onSelect = { item ->
                    nav.navigate(item.route) {
                        popUpTo(Destination.Life.route)
                        launchSingleTop = true
                    }
                },
            )
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Destination.Life.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Life.route) {
                LifeGridScreen(birthDate = settings.birthDate)
            }
            composable(Destination.Year.route) {
                YearCalendarScreen()
            }
            composable(Destination.Progress.route) {
                YearProgressScreen(birthDate = settings.birthDate)
            }
            composable(Destination.Milestones.route) {
                MilestonesListScreen(
                    onAdd = { nav.navigate("milestone/edit/0") },
                    onEdit = { id -> nav.navigate("milestone/edit/$id") },
                )
            }
            composable(
                "milestone/edit/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) {
                MilestoneEditScreen(onDone = { nav.popBackStack() })
            }
        }
    }

    if (showAbout) AboutModal(onDismiss = { showAbout = false })
    if (showSettings) {
        SettingsSheet(
            onDismiss = { showSettings = false },
            onEditBirthDate = { /* date-picker hook — left as a follow-up */ },
            onPinWidget = {
                PinWidgetHelper.requestPin(ctx, YearProgressWidgetReceiver::class.java)
            },
        )
    }
}

private fun currentTitle(route: String?): String = when (route) {
    Destination.Life.route -> "Life"
    Destination.Year.route -> "Year"
    Destination.Progress.route -> "Now"
    Destination.Milestones.route -> "Goals"
    else -> "clndr"
}
