package com.knownassurajit.clndr_widget.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.knownassurajit.clndr_widget.app.shell.ClndrShell
import com.knownassurajit.clndr_widget.app.shell.SettingsViewModel
import com.knownassurajit.clndr_widget.core.designsystem.theme.ClndrTheme
import com.knownassurajit.clndr_widget.core.designsystem.theme.DarkPalette
import com.knownassurajit.clndr_widget.core.designsystem.theme.LightPalette
import com.knownassurajit.clndr_widget.core.designsystem.theme.ThemeMode
import com.knownassurajit.clndr_widget.core.designsystem.theme.rememberSunIsUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.state.collectAsStateWithLifecycle()
            val effectiveMode = if (settings.sunriseAutoEnabled) ThemeMode.SUNRISE_AUTO else settings.themeMode
            val sunIsUp by rememberSunIsUp()
            val systemDark = isSystemInDarkTheme()
            val useDark = when (effectiveMode) {
                ThemeMode.FOLLOW_SYSTEM -> systemDark
                ThemeMode.FORCE_DARK -> true
                ThemeMode.FORCE_LIGHT -> false
                ThemeMode.SUNRISE_AUTO -> !sunIsUp
            }
            val barColor = (if (useDark) DarkPalette.screen else LightPalette.screen).toArgb()
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = if (useDark) {
                        SystemBarStyle.dark(barColor)
                    } else {
                        SystemBarStyle.light(barColor, barColor)
                    },
                    navigationBarStyle = if (useDark) {
                        SystemBarStyle.dark(barColor)
                    } else {
                        SystemBarStyle.light(barColor, barColor)
                    },
                )
            }
            ClndrTheme(mode = effectiveMode, sunIsUp = sunIsUp) {
                ClndrShell(settingsViewModel = settingsViewModel)
            }
        }
    }
}
