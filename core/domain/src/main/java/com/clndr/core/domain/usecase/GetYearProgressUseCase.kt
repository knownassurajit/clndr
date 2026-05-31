package com.clndr.core.domain.usecase

import com.clndr.core.datetime.ProgressBuckets
import com.clndr.core.datetime.ProgressEngine
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

class GetYearProgressUseCase @Inject constructor(
    private val engine: ProgressEngine,
    private val clock: Clock,
) {
    operator fun invoke(birthDate: LocalDate?): Flow<ProgressBuckets> =
        engine.observe(zone = clock.zone, birthDate = birthDate)
}
