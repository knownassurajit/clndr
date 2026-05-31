package com.clndr.app.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.clndr.core.domain.model.Milestone
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilestoneReminderScheduler @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val alarmManager: AlarmManager,
) {

    fun schedule(milestone: Milestone) {
        if (!milestone.reminderEnabled) {
            cancel(milestone.id)
            return
        }
        val triggerAt = computeTriggerMillis(milestone)
        if (triggerAt <= System.currentTimeMillis()) {
            cancel(milestone.id)
            return
        }
        val pi = pendingIntentFor(milestone.id)
        if (canScheduleExact()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    fun cancel(id: Long) {
        alarmManager.cancel(pendingIntentFor(id))
    }

    fun canScheduleExact(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

    private fun computeTriggerMillis(milestone: Milestone): Long {
        val zone: ZoneId = milestone.zoneId
        val time: LocalTime = milestone.targetTime ?: DEFAULT_TIME
        val instant = milestone.targetDate
            .atTime(time)
            .atZone(zone)
            .minusMinutes(milestone.reminderLeadMinutes.toLong())
            .toInstant()
        return instant.toEpochMilli()
    }

    private fun pendingIntentFor(id: Long): PendingIntent {
        val intent = Intent(ctx, MilestoneReminderReceiver::class.java).apply {
            action = ACTION_FIRE
            putExtra(EXTRA_MILESTONE_ID, id)
        }
        return PendingIntent.getBroadcast(
            ctx,
            id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    companion object {
        const val ACTION_FIRE = "com.clndr.app.MILESTONE_FIRE"
        const val EXTRA_MILESTONE_ID = "milestone_id"
        private val DEFAULT_TIME: LocalTime = LocalTime.of(9, 0)
    }
}
