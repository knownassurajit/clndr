package com.knownassurajit.clndr_widget.app.shell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.knownassurajit.clndr_widget.app.settings.SettingsRepository
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrPinTarget
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrSettingsSheet
import com.knownassurajit.clndr_widget.core.designsystem.components.ClndrSettingsState
import com.knownassurajit.clndr_widget.core.designsystem.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ClndrSettingsState(
            birthDate = null,
            themeMode = ThemeMode.FOLLOW_SYSTEM,
            sunriseAutoEnabled = false,
            widgetsFollowAppTheme = true,
        ),
    )
    val state: StateFlow<ClndrSettingsState> = _state.asStateFlow()

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    init {
        combine(
            repo.birthDate,
            repo.themeMode,
            repo.sunriseAutoEnabled,
            repo.widgetsFollowAppTheme,
        ) { birth, theme, sun, follow ->
            ClndrSettingsState(
                birthDate = birth,
                themeMode = theme,
                sunriseAutoEnabled = sun,
                widgetsFollowAppTheme = follow,
            )
        }.onEach {
            _state.value = it
            _ready.value = true
        }.launchIn(viewModelScope)
        viewModelScope.launch { repo.mirrorSidecarAndRefresh() }
    }

    fun setBirthDate(date: LocalDate?) {
        viewModelScope.launch { repo.setBirthDate(date) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repo.setThemeMode(mode) }
    }

    fun setSunriseAuto(enabled: Boolean) {
        viewModelScope.launch { repo.setSunriseAuto(enabled) }
    }

    fun setWidgetsFollowAppTheme(enabled: Boolean) {
        viewModelScope.launch { repo.setWidgetsFollowAppTheme(enabled) }
    }
}

@Composable
fun SettingsSheet(
    onDismiss: () -> Unit,
    onPinWidget: (ClndrPinTarget) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ClndrSettingsSheet(
        state = state,
        onDismiss = onDismiss,
        onSetBirthDate = { date -> viewModel.setBirthDate(date) },
        onSetThemeMode = viewModel::setThemeMode,
        onToggleSunriseAuto = viewModel::setSunriseAuto,
        onToggleWidgetsFollowApp = viewModel::setWidgetsFollowAppTheme,
        onPinWidget = onPinWidget,
    )
}
