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

    /** True once persisted settings have been read at least once — gates the onboarding flash. */
    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    init {
        combine(repo.birthDate, repo.themeMode, repo.sunriseAutoEnabled) { birth, theme, sun ->
            println("CLNDR_TEST: SettingsViewModel combine emit: birth=$birth, theme=$theme, sun=$sun")
            ClndrSettingsState(birthDate = birth, themeMode = theme, sunriseAutoEnabled = sun)
        }.onEach {
            _state.value = it
            _ready.value = true
        }.launchIn(viewModelScope)
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
}

@Composable
fun SettingsSheet(
    onDismiss: () -> Unit,
    onPinWidget: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    ClndrSettingsSheet(
        state = state,
        onDismiss = onDismiss,
        onSetBirthDate = { date -> viewModel.setBirthDate(date) },
        onSetThemeMode = viewModel::setThemeMode,
        onToggleSunriseAuto = viewModel::setSunriseAuto,
        onPinWidget = onPinWidget,
    )
}
