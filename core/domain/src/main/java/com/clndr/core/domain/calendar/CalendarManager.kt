package com.clndr.core.domain.calendar

import com.clndr.core.domain.model.Milestone

interface CalendarManager {
    fun upsert(milestone: Milestone): Long?
    fun delete(calendarEventId: Long): Boolean
}
