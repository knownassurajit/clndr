package com.knownassurajit.clndr_widget.feature.milestones.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knownassurajit.clndr_widget.core.domain.calendar.CalendarManager
import com.knownassurajit.clndr_widget.core.domain.repository.MilestonesRepository
import com.knownassurajit.clndr_widget.core.domain.scheduler.MilestoneScheduler
import com.knownassurajit.clndr_widget.core.domain.usecase.SaveMilestoneUseCase
import com.knownassurajit.clndr_widget.feature.milestones.model.MilestoneDraft
import com.knownassurajit.clndr_widget.feature.milestones.model.toDraft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MilestoneEditState(
    val draft: MilestoneDraft = MilestoneDraft(),
    val isSaving: Boolean = false,
    val errors: Map<String, String> = emptyMap(),
)

sealed interface MilestoneEditEffect {
    data object NavigateBack : MilestoneEditEffect
    data class RequestExactAlarmPermission(val deepLinkAction: String) : MilestoneEditEffect
}

@HiltViewModel
class MilestoneEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MilestonesRepository,
    private val saveMilestone: SaveMilestoneUseCase,
    private val scheduler: MilestoneScheduler,
    private val calendarManager: CalendarManager,
) : ViewModel() {

    private val _state = MutableStateFlow(MilestoneEditState())
    val state: StateFlow<MilestoneEditState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<MilestoneEditEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    init {
        val id: Long = savedStateHandle.get<Long>("id") ?: 0L
        if (id != 0L) {
            viewModelScope.launch {
                val existing = repository.getById(id) ?: return@launch
                _state.update { it.copy(draft = existing.toDraft()) }
            }
        }
    }

    fun update(transform: (MilestoneDraft) -> MilestoneDraft) {
        _state.update { it.copy(draft = transform(it.draft), errors = emptyMap()) }
    }

    fun save() {
        val draft = _state.value.draft
        val errors = draft.validate()
        if (errors.isNotEmpty()) {
            _state.update { it.copy(errors = errors) }
            return
        }
        if (draft.reminderEnabled && !scheduler.canScheduleExact()) {
            _effects.tryEmit(
                MilestoneEditEffect.RequestExactAlarmPermission(
                    android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                )
            )
        }
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            saveMilestone(draft.toMilestone(), draft.mirrorToCalendar)
            _state.update { it.copy(isSaving = false) }
            _effects.tryEmit(MilestoneEditEffect.NavigateBack)
        }
    }

    fun delete() {
        val id = _state.value.draft.id
        if (id == 0L) {
            _effects.tryEmit(MilestoneEditEffect.NavigateBack)
            return
        }
        viewModelScope.launch {
            val existing = repository.getById(id) ?: return@launch
            runCatching { scheduler.cancel(id) }
            existing.calendarEventId?.let { eventId ->
                runCatching { calendarManager.delete(eventId) }
            }
            repository.delete(existing)
            _effects.tryEmit(MilestoneEditEffect.NavigateBack)
        }
    }
}
