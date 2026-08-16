package com.knownassurajit.clndr_widget.app.reminders

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.provider.CalendarContract
import com.knownassurajit.clndr_widget.core.domain.calendar.CalendarManager
import com.knownassurajit.clndr_widget.core.domain.model.Milestone
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Optional mirror of a milestone into the system calendar.
 * Requires WRITE_CALENDAR (and READ_CALENDAR to pick a writable calendar) at call time.
 */
@Singleton
class CalendarMirror @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
) : CalendarManager {

    override fun upsert(milestone: Milestone): Long? {
        val resolver = context.contentResolver
        val calendarId = resolveWritableCalendarId(resolver) ?: return null
        val values = eventValues(milestone, calendarId)
        val existingId = milestone.calendarEventId
        val eventId = if (existingId != null && existingId > 0L) {
            val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, existingId)
            val updated = runCatching { resolver.update(uri, values, null, null) }.getOrDefault(0)
            if (updated > 0) existingId else insertEvent(resolver, values) ?: return null
        } else {
            insertEvent(resolver, values) ?: return null
        }
        syncReminders(resolver, eventId, milestone)
        return eventId
    }

    override fun delete(calendarEventId: Long): Boolean {
        val resolver = context.contentResolver
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calendarEventId)
        return runCatching { resolver.delete(uri, null, null) > 0 }.getOrDefault(false)
    }

    private fun insertEvent(resolver: ContentResolver, values: ContentValues): Long? =
        runCatching {
            resolver.insert(CalendarContract.Events.CONTENT_URI, values)?.lastPathSegment?.toLongOrNull()
        }.getOrNull()

    private fun eventValues(milestone: Milestone, calendarId: Long): ContentValues {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, milestone.title)
            milestone.description?.let { put(CalendarContract.Events.DESCRIPTION, it) }
            put(CalendarContract.Events.HAS_ALARM, if (milestone.reminderEnabled) 1 else 0)
        }
        val time = milestone.targetTime
        if (time != null) {
            val startMs = milestone.targetDate
                .atTime(time)
                .atZone(milestone.zoneId)
                .toInstant()
                .toEpochMilli()
            values.put(CalendarContract.Events.ALL_DAY, 0)
            values.put(CalendarContract.Events.DTSTART, startMs)
            values.put(CalendarContract.Events.DTEND, startMs + ONE_HOUR_MS)
            values.put(CalendarContract.Events.EVENT_TIMEZONE, milestone.zoneId.id)
        } else {
            val startMs = milestone.targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val endMs = milestone.targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            values.put(CalendarContract.Events.ALL_DAY, 1)
            values.put(CalendarContract.Events.DTSTART, startMs)
            values.put(CalendarContract.Events.DTEND, endMs)
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC")
        }
        return values
    }

    private fun syncReminders(resolver: ContentResolver, eventId: Long, milestone: Milestone) {
        runCatching {
            resolver.delete(
                CalendarContract.Reminders.CONTENT_URI,
                "${CalendarContract.Reminders.EVENT_ID}=?",
                arrayOf(eventId.toString()),
            )
        }
        if (!milestone.reminderEnabled) return
        runCatching {
            resolver.insert(
                CalendarContract.Reminders.CONTENT_URI,
                ContentValues().apply {
                    put(CalendarContract.Reminders.EVENT_ID, eventId)
                    put(CalendarContract.Reminders.MINUTES, milestone.reminderLeadMinutes)
                    put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                },
            )
        }
    }

    private fun resolveWritableCalendarId(resolver: ContentResolver): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
        )
        val selection = "${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ?"
        val args = arrayOf(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString())
        return runCatching {
            resolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                args,
                "${CalendarContract.Calendars._ID} ASC",
            )?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) else null
            }
        }.getOrNull()
    }

    companion object {
        private const val ONE_HOUR_MS = 60L * 60L * 1_000L
    }
}
