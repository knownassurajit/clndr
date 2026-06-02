package com.clndr.feature.lifegrid.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clndr.core.datetime.ProgressBuckets
import com.clndr.core.domain.usecase.GetYearProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class YearProgressViewModel @Inject constructor(
    private val getYearProgress: GetYearProgressUseCase,
) : ViewModel() {

    private val _birth = MutableStateFlow<LocalDate?>(null)
    private val _buckets = MutableStateFlow(ProgressBuckets.EMPTY)
    val buckets: StateFlow<ProgressBuckets> = _buckets.asStateFlow()

    init {
        // Always observe the live calendar cycles. The engine accepts a null birth date —
        // decade/year/month/week/day stay calendar-relative (matching the web design), and a
        // bound birth date only adds the otherwise-unused era track.
        @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
        _birth.flatMapLatest { birth ->
            getYearProgress(birth)
        }.onEach { _buckets.value = it }
            .launchIn(viewModelScope)
    }

    fun bindBirthDate(birthDate: LocalDate?) {
        _birth.value = birthDate
    }
}
