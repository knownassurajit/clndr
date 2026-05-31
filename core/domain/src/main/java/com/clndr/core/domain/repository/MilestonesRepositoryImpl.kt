package com.clndr.core.domain.repository

import com.clndr.core.database.MilestoneDao
import com.clndr.core.domain.model.Milestone
import com.clndr.core.domain.model.toDomain
import com.clndr.core.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MilestonesRepositoryImpl @Inject constructor(
    private val dao: MilestoneDao,
    private val clock: Clock,
) : MilestonesRepository {

    override fun observeAll(): Flow<List<Milestone>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override fun observeUpcoming(today: LocalDate): Flow<List<Milestone>> =
        dao.observeUpcoming(today.toEpochDay()).map { rows -> rows.map { it.toDomain() } }

    override fun observePast(today: LocalDate): Flow<List<Milestone>> =
        dao.observePast(today.toEpochDay()).map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: Long): Milestone? =
        dao.getById(id)?.toDomain()

    override suspend fun upsert(milestone: Milestone): Long {
        val entity = milestone.toEntity(clock.instant())
        return if (milestone.id == 0L) {
            dao.insert(entity)
        } else {
            dao.update(entity)
            milestone.id
        }
    }

    override suspend fun delete(milestone: Milestone) {
        dao.delete(milestone.toEntity(clock.instant()))
    }

    override suspend fun activeReminders(today: LocalDate): List<Milestone> =
        dao.allActiveReminders(today.toEpochDay()).map { it.toDomain() }
}
