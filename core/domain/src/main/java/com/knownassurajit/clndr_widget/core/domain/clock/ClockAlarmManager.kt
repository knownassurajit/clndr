package com.knownassurajit.clndr_widget.core.domain.clock

import com.knownassurajit.clndr_widget.core.domain.model.Milestone

interface ClockAlarmManager {
    fun setAlarm(milestone: Milestone)
}
