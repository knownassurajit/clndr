package com.clndr.app.reminders

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.provider.CalendarContract
import com.clndr.core.domain.calendar.CalendarManager
import com.clndr.core.domain.model.Milestone
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Optional mirror of a milestone into the system calendar.
 * Requires WRITE_CALENDAR at call time — caller is responsible for the runtime permission.
 */
@Singleton
class CalendarMirror @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
) : CalendarManager {

    override fun upsert(milestone: Milestone): Long? {
        val resolver: ContentResolver = context.contentResolver
        // Honor the chosen time when set; otherwise fall back to start-of-day.
        val startMs = milestone.targetTime
            ?.let { milestone.targetDate.atTime(it).atZone(milestone.zoneId) }
            ?.toInstant()?.toEpochMilli()
            ?: milestone.targetDate.atStartOfDay(milestone.zoneId).toInstant().toEpochMilli()
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMs)
            put(CalendarContract.Events.DTEND, startMs + ONE_HOUR_MS)
            put(CalendarContract.Events.TITLE, milestone.title)
            milestone.description?.let { put(CalendarContract.Events.DESCRIPTION, it) }
            put(CalendarContract.Events.EVENT_TIMEZONE, milestone.zoneId.id)
            put(CalendarContract.Events.CALENDAR_ID, DEFAULT_CALENDAR_ID)
        }
        return runCatching {
            val uri = resolver.insert(CalendarContract.Events.CONTENT_URI, values) ?: return null
            uri.lastPathSegment?.toLongOrNull()
        }.getOrNull()
    }

    override fun delete(calendarEventId: Long): Boolean {
        val resolver: ContentResolver = context.contentResolver
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calendarEventId)
        return runCatching {
            resolver.delete(uri, null, null) > 0
        }.getOrDefault(false)
    }

    companion object {
        private const val ONE_HOUR_MS = 60L * 60L * 1_000L
        private const val DEFAULT_CALENDAR_ID = 1L
    }
}
