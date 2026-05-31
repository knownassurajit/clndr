package com.clndr.core.domain.usecase

import com.clndr.core.domain.model.Milestone
import com.clndr.core.domain.repository.MilestonesRepository
import javax.inject.Inject

class SaveMilestoneUseCase @Inject constructor(
    private val repository: MilestonesRepository,
) {
    suspend operator fun invoke(milestone: Milestone): Long = repository.upsert(milestone)
}
