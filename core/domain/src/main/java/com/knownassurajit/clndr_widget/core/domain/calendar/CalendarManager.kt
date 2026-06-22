package com.knownassurajit.clndr_widget.core.domain.calendar

import com.knownassurajit.clndr_widget.core.domain.model.Milestone

interface CalendarManager {
    fun upsert(milestone: Milestone): Long?
    fun delete(calendarEventId: Long): Boolean
}
