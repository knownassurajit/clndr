package com.knownassurajit.clndr_widget.app.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.knownassurajit.clndr_widget.app.widget.PinWidgetHelper
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrNavBar
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrNavItem
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrPinTarget
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrTopBar
import com.knownassurajit.clndr_widget.core.designsystem.theme.clndr
import com.knownassurajit.clndr_widget.feature.lifegrid.LifeGridScreen
import com.knownassurajit.clndr_widget.feature.lifegrid.YearCalendarScreen
import com.knownassurajit.clndr_widget.feature.lifegrid.YearProgressScreen
import com.knownassurajit.clndr_widget.feature.milestones.MilestoneEditScreen
import com.knownassurajit.clndr_widget.feature.milestones.MilestonesListScreen
import com.knownassurajit.clndr_widget.feature.widgets.goals.GoalsWidgetReceiver
import com.knownassurajit.clndr_widget.feature.widgets.life.LifeMatrixWidgetReceiver
import com.knownassurajit.clndr_widget.feature.widgets.year.YearCalendarWidgetReceiver
import com.knownassurajit.clndr_widget.feature.widgets.year.YearProgressWidgetReceiver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClndrShell(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val ready by settingsViewModel.ready.collectAsStateWithLifecycle()
    val settings by settingsViewModel.state.collectAsStateWithLifecycle()

    if (!ready) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.clndr.screen))
        return
    }

    if (settings.birthDate == null) {
        OnboardingScreen(onDone = { settingsViewModel.setBirthDate(it) })
        return
    }

    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val ctx = LocalContext.current

    var showAbout by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val items = remember {
        Destination.all.map { ClndrNavItem(it.route, it.label, it.icon) }
    }

    val isEditingMilestone = currentRoute?.startsWith("milestone/edit") == true

    Scaffold(
        containerColor = MaterialTheme.clndr.screen,
        topBar = {
            ClndrTopBar(
                title = currentTitle(currentRoute),
                onInfo = { showAbout = true },
                onSettings = { showSettings = true },
            )
        },
        bottomBar = {
            if (!isEditingMilestone) {
                ClndrNavBar(
                    items = items,
                    currentRoute = currentRoute,
                    onSelect = { item ->
                        nav.navigate(item.route) {
                            popUpTo(Destination.Progress.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Destination.Progress.route,
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
            onPinWidget = { target ->
                val provider = when (target) {
                    ClndrPinTarget.YearProgress -> YearProgressWidgetReceiver::class.java
                    ClndrPinTarget.YearCalendar -> YearCalendarWidgetReceiver::class.java
                    ClndrPinTarget.Life -> LifeMatrixWidgetReceiver::class.java
                    ClndrPinTarget.Goals -> GoalsWidgetReceiver::class.java
                }
                PinWidgetHelper.requestPin(ctx, provider)
            },
        )
    }
}

private fun currentTitle(route: String?): String = when (route) {
    Destination.Life.route -> "Your Life"
    Destination.Year.route -> "This Year"
    Destination.Progress.route -> "In Progress"
    Destination.Milestones.route -> "Milestones"
    else -> if (route?.startsWith("milestone/edit") == true) "Milestone" else "clndr"
}
