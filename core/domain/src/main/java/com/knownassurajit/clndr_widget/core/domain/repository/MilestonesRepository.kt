package com.knownassurajit.clndr_widget.core.domain.repository

import com.knownassurajit.clndr_widget.core.domain.model.Milestone
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MilestonesRepository {

    fun observeAll(): Flow<List<Milestone>>

    fun observeUpcoming(today: LocalDate): Flow<List<Milestone>>

    fun observePast(today: LocalDate): Flow<List<Milestone>>

    suspend fun getById(id: Long): Milestone?

    suspend fun upsert(milestone: Milestone): Long

    suspend fun delete(milestone: Milestone)

    suspend fun activeReminders(today: LocalDate): List<Milestone>
}
