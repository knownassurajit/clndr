package com.clndr.feature.milestones.model

import com.clndr.core.domain.model.Milestone
import java.time.LocalDate
import kotlin.math.abs

data class MilestoneUi(
    val id: Long,
    val title: String,
    val description: String?,
    val targetDate: LocalDate,
    val countdownLabel: String,
    val isPast: Boolean,
)

fun Milestone.toUi(today: LocalDate): MilestoneUi {
    val days = daysUntil(today)
    val label = when {
        days == 0L -> "Today"
        days > 0L -> "D-${abs(days)}"
        else -> "D+${abs(days)}"
    }
    return MilestoneUi(
        id = id,
        title = title,
        description = description,
        targetDate = targetDate,
        countdownLabel = label,
        isPast = isPast(today),
    )
}
