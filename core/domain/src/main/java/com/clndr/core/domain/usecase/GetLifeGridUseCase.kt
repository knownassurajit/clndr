package com.clndr.core.domain.usecase

import com.clndr.core.datetime.Granularity
import com.clndr.core.datetime.LifeGridCalculator
import com.clndr.core.datetime.LifeGridSpec
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

data class LifeGridSnapshot(
    val packedStates: IntArray,
    val totalCells: Int,
    val currentIndex: Int,
    val granularity: Granularity,
)

class GetLifeGridUseCase @Inject constructor(
    private val calculator: LifeGridCalculator,
    private val clock: Clock,
) {
    operator fun invoke(birthDate: LocalDate, granularity: Granularity): LifeGridSnapshot {
        val today = LocalDate.now(clock)
        val spec = LifeGridSpec(birthDate = birthDate, granularity = granularity, today = today)
        return LifeGridSnapshot(
            packedStates = calculator.stateBitset(spec),
            totalCells = calculator.totalCells(spec),
            currentIndex = calculator.currentIndex(spec),
            granularity = granularity,
        )
    }
}
