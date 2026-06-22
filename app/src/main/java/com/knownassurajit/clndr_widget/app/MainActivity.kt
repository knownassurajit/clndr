package com.knownassurajit.clndr_widget.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.knownassurajit.clndr_widget.app.shell.ClndrShell
import com.knownassurajit.clndr_widget.app.shell.SettingsViewModel
import com.knownassurajit.clndr_widget.core.designsystem.theme.ClndrTheme
import com.knownassurajit.clndr_widget.core.designsystem.theme.ThemeMode
import com.knownassurajit.clndr_widget.core.designsystem.theme.rememberSunIsUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.state.collectAsState()
            val effectiveMode = if (settings.sunriseAutoEnabled) ThemeMode.SUNRISE_AUTO else settings.themeMode
            val sunIsUp by rememberSunIsUp()
            ClndrTheme(mode = effectiveMode, sunIsUp = sunIsUp) {
                ClndrShell(settingsViewModel = settingsViewModel)
            }
        }
    }
}
