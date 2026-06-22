package com.knownassurajit.clndr_widget.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class MilestoneDaoTest {

    private lateinit var db: ClndrDatabase
    private lateinit var dao: MilestoneDao

    @Before
    fun setUp() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, ClndrDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.milestones()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndObserveAllReturnsSingleEntry() = runTest {
        val entity = sample(targetDate = LocalDate.of(2026, 12, 31))
        dao.insert(entity)
        val rows = dao.observeAll().first()
        assertThat(rows).hasSize(1)
        assertThat(rows.first().title).isEqualTo("Demo")
    }

    @Test
    fun upcomingExcludesPastEvents() = runTest {
        val today = LocalDate.of(2026, 5, 31).toEpochDay()
        dao.insert(sample(targetDate = LocalDate.of(2025, 1, 1)))
        dao.insert(sample(targetDate = LocalDate.of(2027, 1, 1), title = "Future"))
        val upcoming = dao.observeUpcoming(today).first()
        assertThat(upcoming).hasSize(1)
        assertThat(upcoming.first().title).isEqualTo("Future")
    }

    @Test
    fun activeRemindersFiltersByEnabledFlag() = runTest {
        val today = LocalDate.of(2026, 5, 31).toEpochDay()
        dao.insert(sample(targetDate = LocalDate.of(2026, 12, 1), reminderEnabled = true))
        dao.insert(sample(targetDate = LocalDate.of(2026, 12, 1), reminderEnabled = false, title = "Off"))
        val active = dao.allActiveReminders(today)
        assertThat(active).hasSize(1)
        assertThat(active.first().reminderEnabled).isTrue()
    }

    private fun sample(
        targetDate: LocalDate,
        title: String = "Demo",
        reminderEnabled: Boolean = true,
    ) = MilestoneEntity(
        title = title,
        description = null,
        targetEpochDay = targetDate.toEpochDay(),
        targetTimeSecOfDay = null,
        zoneId = "UTC",
        reminderEnabled = reminderEnabled,
        reminderLeadMinutes = 0,
        notificationChannelId = "clndr.milestones",
        calendarEventId = null,
        createdAtEpochSec = 0L,
        updatedAtEpochSec = 0L,
        color = null,
    )
}
