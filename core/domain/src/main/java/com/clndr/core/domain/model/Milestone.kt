package com.clndr.core.domain.model

import com.clndr.core.database.MilestoneEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class Milestone(
    val id: Long,
    val title: String,
    val description: String?,
    val targetDate: LocalDate,
    val targetTime: LocalTime?,
    val zoneId: ZoneId,
    val reminderEnabled: Boolean,
    val reminderLeadMinutes: Int,
    val calendarEventId: Long?,
    val color: Int?,
) {
    fun isPast(today: LocalDate): Boolean = targetDate.isBefore(today)

    fun daysUntil(today: LocalDate): Long = targetDate.toEpochDay() - today.toEpochDay()

    companion object {
        const val DEFAULT_CHANNEL_ID = "clndr.milestones"
    }
}

fun MilestoneEntity.toDomain(): Milestone = Milestone(
    id = id,
    title = title,
    description = description,
    targetDate = LocalDate.ofEpochDay(targetEpochDay),
    targetTime = targetTimeSecOfDay?.let { LocalTime.ofSecondOfDay(it.toLong()) },
    zoneId = ZoneId.of(zoneId),
    reminderEnabled = reminderEnabled,
    reminderLeadMinutes = reminderLeadMinutes,
    calendarEventId = calendarEventId,
    color = color,
)

fun Milestone.toEntity(now: java.time.Instant): MilestoneEntity = MilestoneEntity(
    id = id,
    title = title,
    description = description,
    targetEpochDay = targetDate.toEpochDay(),
    targetTimeSecOfDay = targetTime?.toSecondOfDay(),
    zoneId = zoneId.id,
    reminderEnabled = reminderEnabled,
    reminderLeadMinutes = reminderLeadMinutes,
    notificationChannelId = Milestone.DEFAULT_CHANNEL_ID,
    calendarEventId = calendarEventId,
    createdAtEpochSec = now.epochSecond,
    updatedAtEpochSec = now.epochSecond,
    color = color,
)
