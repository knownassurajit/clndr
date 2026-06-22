package com.knownassurajit.clndr_widget.app.reminders

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.knownassurajit.clndr_widget.app.MainActivity
import com.knownassurajit.clndr_widget.app.R
import com.knownassurajit.clndr_widget.core.domain.repository.MilestonesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MilestoneReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: MilestonesRepository

    @Inject lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != MilestoneReminderScheduler.ACTION_FIRE) return
        val id = intent.getLongExtra(MilestoneReminderScheduler.EXTRA_MILESTONE_ID, -1L)
        if (id < 0) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val milestone = repository.getById(id) ?: return@launch
                val tap = PendingIntent.getActivity(
                    context,
                    id.toInt(),
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE,
                )
                val notification = NotificationCompat.Builder(context, NotificationChannels.MILESTONES)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(milestone.title)
                    .setContentText(milestone.description ?: milestone.targetDate.toString())
                    .setContentIntent(tap)
                    .setAutoCancel(true)
                    .build()
                notificationManager.notify(id.toInt(), notification)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
