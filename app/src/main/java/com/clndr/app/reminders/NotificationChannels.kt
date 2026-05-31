package com.clndr.app.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {

    const val MILESTONES = "clndr.milestones"

    fun ensureCreated(ctx: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = ctx.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            MILESTONES,
            "Milestones",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Reminders for your milestones"
        }
        nm.createNotificationChannel(channel)
    }
}
