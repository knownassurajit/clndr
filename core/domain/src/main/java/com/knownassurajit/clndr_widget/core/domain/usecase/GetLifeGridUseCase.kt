package com.knownassurajit.clndr_widget.core.domain.usecase

import com.knownassurajit.clndr_widget.core.datetime.Granularity
import com.knownassurajit.clndr_widget.core.datetime.LifeGridCalculator
import com.knownassurajit.clndr_widget.core.datetime.LifeGridSpec
import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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
        // No fixed lifespan: render only what's lived plus a short headroom of unspent
        // cells. This keeps the grid (and its bitset) bounded — a day grid for a 110-year
        // horizon is ~40k cells; lived + headroom is a fraction of that.
        val age = ChronoUnit.YEARS.between(birthDate, today).toInt().coerceAtLeast(0)
        val horizon = (age + HEADROOM_YEARS).coerceIn(1, LifeGridSpec.MAX_LIFESPAN_YEARS)
        val spec = LifeGridSpec(
            birthDate = birthDate,
            lifeSpanYears = horizon,
            granularity = granularity,
            today = today,
        )
        return LifeGridSnapshot(
            packedStates = calculator.stateBitset(spec),
            totalCells = calculator.totalCells(spec),
            currentIndex = calculator.currentIndex(spec),
            granularity = granularity,
        )
    }

    companion object {
        /** Years of unspent cells shown beyond the user's current age. */
        const val HEADROOM_YEARS = 15
    }
}
