package com.knownassurajit.clndr_widget.app.reminders

import android.content.Intent
import android.provider.AlarmClock
import com.knownassurajit.clndr_widget.core.domain.clock.ClockAlarmManager
import com.knownassurajit.clndr_widget.core.domain.model.Milestone
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClockAlarmMirror @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
) : ClockAlarmManager {

    override fun setAlarm(milestone: Milestone) {
        val time = milestone.targetTime ?: DEFAULT_TIME
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, time.hour)
            putExtra(AlarmClock.EXTRA_MINUTES, time.minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, milestone.title)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }

    companion object {
        private val DEFAULT_TIME: LocalTime = LocalTime.of(9, 0)
    }
}
