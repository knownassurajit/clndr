package com.knownassurajit.clndr_widget.app.reminders

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.knownassurajit.clndr_widget.core.domain.repository.MilestonesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Clock
import java.time.LocalDate

@HiltWorker
class BootRescheduleWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val repository: MilestonesRepository,
    private val scheduler: MilestoneReminderScheduler,
    private val clock: Clock,
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now(clock)
        val active = repository.activeReminders(today)
        active.forEach { scheduler.schedule(it) }
        return Result.success()
    }
}
