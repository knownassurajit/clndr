package com.clndr.core.domain.usecase

import com.clndr.core.domain.calendar.CalendarManager
import com.clndr.core.domain.model.Milestone
import com.clndr.core.domain.repository.MilestonesRepository
import com.clndr.core.domain.scheduler.MilestoneScheduler
import javax.inject.Inject

class SaveMilestoneUseCase @Inject constructor(
    private val repository: MilestonesRepository,
    private val scheduler: MilestoneScheduler,
    private val calendarManager: CalendarManager,
) {
    suspend operator fun invoke(milestone: Milestone, mirrorToCalendar: Boolean): Long {
        var updatedMilestone = milestone
        if (mirrorToCalendar) {
            val eventId = calendarManager.upsert(milestone)
            if (eventId != null) {
                updatedMilestone = milestone.copy(calendarEventId = eventId)
            }
        } else {
            milestone.calendarEventId?.let { eventId ->
                calendarManager.delete(eventId)
            }
            updatedMilestone = milestone.copy(calendarEventId = null)
        }

        val id = repository.upsert(updatedMilestone)
        val savedMilestone = updatedMilestone.copy(id = id)
        scheduler.schedule(savedMilestone)
        return id
    }
}
