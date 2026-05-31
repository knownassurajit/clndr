package com.clndr.app.shell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clndr.app.settings.SettingsRepository
import com.clndr.core.designsystem.components.ClndrSettingsSheet
import com.clndr.core.designsystem.components.ClndrSettingsState
import com.clndr.core.designsystem.theme.ThemeMode
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
        ClndrSettingsState(birthDate = null, themeMode = ThemeMode.FOLLOW_SYSTEM, sunriseAutoEnabled = false),
    )
    val state: StateFlow<ClndrSettingsState> = _state.asStateFlow()

    init {
        combine(repo.birthDate, repo.themeMode, repo.sunriseAutoEnabled) { birth, theme, sun ->
            ClndrSettingsState(birthDate = birth, themeMode = theme, sunriseAutoEnabled = sun)
        }.onEach { _state.value = it }.launchIn(viewModelScope)
    }

    fun setBirthDate(date: LocalDate?) {
        viewModelScope.launch { repo.setBirthDate(date) }
    }

    fun cycleThemeMode() {
        viewModelScope.launch {
            val next = when (_state.value.themeMode) {
                ThemeMode.FOLLOW_SYSTEM -> ThemeMode.FORCE_LIGHT
                ThemeMode.FORCE_LIGHT -> ThemeMode.FORCE_DARK
                ThemeMode.FORCE_DARK -> ThemeMode.SUNRISE_AUTO
                ThemeMode.SUNRISE_AUTO -> ThemeMode.FOLLOW_SYSTEM
            }
            repo.setThemeMode(next)
        }
    }

    fun setSunriseAuto(enabled: Boolean) {
        viewModelScope.launch { repo.setSunriseAuto(enabled) }
    }
}

@Composable
fun SettingsSheet(
    onDismiss: () -> Unit,
    onEditBirthDate: () -> Unit,
    onPinWidget: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    ClndrSettingsSheet(
        state = state,
        onDismiss = onDismiss,
        onEditBirthDate = onEditBirthDate,
        onToggleSunriseAuto = viewModel::setSunriseAuto,
        onCycleThemeMode = viewModel::cycleThemeMode,
        onPinWidget = onPinWidget,
    )
}
