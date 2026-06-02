package com.clndr.core.domain.scheduler

import com.clndr.core.domain.model.Milestone

interface MilestoneScheduler {
    fun schedule(milestone: Milestone)
    fun cancel(id: Long)
    fun canScheduleExact(): Boolean
}
