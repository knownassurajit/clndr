package com.knownassurajit.clndr_widget.core.domain.scheduler

import com.knownassurajit.clndr_widget.core.domain.model.Milestone

interface MilestoneScheduler {
    fun schedule(milestone: Milestone)
    fun cancel(id: Long)
    fun canScheduleExact(): Boolean
}
