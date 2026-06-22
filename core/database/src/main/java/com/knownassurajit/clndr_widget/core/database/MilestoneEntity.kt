package com.knownassurajit.clndr_widget.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "milestones",
    indices = [
        Index(value = ["targetEpochDay"]),
        Index(value = ["title"]),
    ],
)
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val targetEpochDay: Long,
    val targetTimeSecOfDay: Int?,
    val zoneId: String,
    val reminderEnabled: Boolean,
    val reminderLeadMinutes: Int,
    val notificationChannelId: String,
    val calendarEventId: Long?,
    val createdAtEpochSec: Long,
    val updatedAtEpochSec: Long,
    val color: Int?,
)
