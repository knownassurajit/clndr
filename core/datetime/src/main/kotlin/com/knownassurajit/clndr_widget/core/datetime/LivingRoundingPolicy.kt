package com.knownassurajit.clndr_widget.core.datetime

import java.time.LocalDate

object LivingRoundingPolicy {

    fun classify(cellStart: LocalDate, cellEndInclusive: LocalDate, today: LocalDate): CellState =
        when {
            cellEndInclusive.isBefore(today) -> CellState.PAST
            cellStart.isAfter(today) -> CellState.FUTURE
            else -> CellState.PRESENT
        }
}
