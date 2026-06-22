package com.knownassurajit.clndr_widget.feature.milestones.model

import com.knownassurajit.clndr_widget.core.domain.model.Milestone
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class MilestoneDraft(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val targetDate: LocalDate = LocalDate.now(),
    val targetTime: LocalTime? = null,
    val reminderEnabled: Boolean = false,
    val reminderLeadMinutes: Int = 0,
    val mirrorToCalendar: Boolean = false,
) {
    fun toMilestone(zone: ZoneId = ZoneId.systemDefault()): Milestone = Milestone(
        id = id,
        title = title.trim(),
        description = description.takeIf { it.isNotBlank() },
        targetDate = targetDate,
        targetTime = targetTime,
        zoneId = zone,
        reminderEnabled = reminderEnabled,
        reminderLeadMinutes = reminderLeadMinutes,
        calendarEventId = null,
        color = null,
    )

    fun validate(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (title.isBlank()) errors["title"] = "Title is required"
        if (reminderLeadMinutes < 0) errors["lead"] = "Lead must be non-negative"
        return errors
    }
}

fun Milestone.toDraft(): MilestoneDraft = MilestoneDraft(
    id = id,
    title = title,
    description = description.orEmpty(),
    targetDate = targetDate,
    targetTime = targetTime,
    reminderEnabled = reminderEnabled,
    reminderLeadMinutes = reminderLeadMinutes,
    mirrorToCalendar = calendarEventId != null,
)
