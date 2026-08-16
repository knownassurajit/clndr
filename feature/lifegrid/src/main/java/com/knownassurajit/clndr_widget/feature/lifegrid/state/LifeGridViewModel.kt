package com.knownassurajit.clndr_widget.feature.lifegrid.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knownassurajit.clndr_widget.core.datetime.Granularity
import com.knownassurajit.clndr_widget.core.domain.usecase.GetLifeGridUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LifeGridViewModel @Inject constructor(
    private val getLifeGrid: GetLifeGridUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LifeGridState())
    val state: StateFlow<LifeGridState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<LifeGridEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun onIntent(intent: LifeGridIntent) {
        when (intent) {
            is LifeGridIntent.SetGranularity -> {
                _state.update { it.copy(granularity = intent.granularity) }
                recompute()
            }
            is LifeGridIntent.SetBirthDate -> {
                _state.update { it.copy(birthDate = intent.date) }
                recompute()
            }
            LifeGridIntent.Refresh -> recompute()
        }
    }

    fun bindBirthDate(birthDate: LocalDate?) {
        if (_state.value.birthDate != birthDate) {
            _state.update { it.copy(birthDate = birthDate) }
            recompute()
        }
    }

    private fun recompute() {
        val birth = _state.value.birthDate ?: run {
            _state.update { it.copy(isLoading = false, packedStates = IntArray(0), totalCells = 0, currentIndex = -1) }
            return
        }
        viewModelScope.launch {
            val granularity = _state.value.granularity
            val snapshot = withContext(Dispatchers.Default) {
                getLifeGrid(birth, granularity)
            }
            _state.update {
                it.copy(
                    packedStates = snapshot.packedStates,
                    totalCells = snapshot.totalCells,
                    currentIndex = snapshot.currentIndex,
                    today = snapshot.today,
                    isLoading = false,
                )
            }
            _effects.tryEmit(LifeGridEffect.ScrollToCurrent)
        }
    }

    @Suppress("unused")
    private val defaultGranularity = Granularity.WEEKS
}
