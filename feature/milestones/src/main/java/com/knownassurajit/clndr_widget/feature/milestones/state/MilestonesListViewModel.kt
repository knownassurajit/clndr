package com.knownassurajit.clndr_widget.feature.milestones.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knownassurajit.clndr_widget.core.domain.repository.MilestonesRepository
import com.knownassurajit.clndr_widget.feature.milestones.model.MilestoneUi
import com.knownassurajit.clndr_widget.feature.milestones.model.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

data class MilestonesListState(
    val upcoming: List<MilestoneUi> = emptyList(),
    val past: List<MilestoneUi> = emptyList(),
)

@HiltViewModel
class MilestonesListViewModel @Inject constructor(
    private val repository: MilestonesRepository,
    private val clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow(MilestonesListState())
    val state: StateFlow<MilestonesListState> = _state.asStateFlow()

    init {
        val today = LocalDate.now(clock)
        combine(
            repository.observeUpcoming(today),
            repository.observePast(today),
        ) { upcoming, past ->
            MilestonesListState(
                upcoming = upcoming.map { it.toUi(today) },
                past = past.map { it.toUi(today) },
            )
        }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            val item = repository.getById(id) ?: return@launch
            repository.delete(item)
        }
    }
}
