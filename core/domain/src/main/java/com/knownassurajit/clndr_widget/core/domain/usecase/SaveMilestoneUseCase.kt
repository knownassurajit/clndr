package com.knownassurajit.clndr_widget.core.domain.usecase

import com.knownassurajit.clndr_widget.core.domain.calendar.CalendarManager
import com.knownassurajit.clndr_widget.core.domain.model.Milestone
import com.knownassurajit.clndr_widget.core.domain.repository.MilestonesRepository
import com.knownassurajit.clndr_widget.core.domain.scheduler.MilestoneScheduler
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
