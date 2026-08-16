package com.knownassurajit.clndr_widget.feature.lifegrid.state

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class YearCalendarViewModel @Inject constructor(
    clock: Clock,
) : ViewModel() {
    val today: LocalDate = LocalDate.now(clock)
}
