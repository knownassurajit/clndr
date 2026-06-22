package com.knownassurajit.clndr_widget.core.datetime

import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class LifeGridSpec(
    val birthDate: LocalDate,
    val lifeSpanYears: Int = DEFAULT_LIFESPAN_YEARS,
    val granularity: Granularity,
    val today: LocalDate,
) {
    init {
        require(lifeSpanYears in 1..MAX_LIFESPAN_YEARS) {
            "lifeSpanYears must be in 1..$MAX_LIFESPAN_YEARS, was $lifeSpanYears"
        }
    }

    companion object {
        const val DEFAULT_LIFESPAN_YEARS = 110
        const val MAX_LIFESPAN_YEARS = 200
    }
}

data class LifeGridCell(
    val index: Int,
    val state: CellState,
    val startDate: LocalDate,
    val endDate: LocalDate,
)

class LifeGridCalculator(
    @Suppress("unused") private val clock: Clock = Clock.systemDefaultZone(),
) {

    fun totalCells(spec: LifeGridSpec): Int {
        val end = spec.birthDate.plusYears(spec.lifeSpanYears.toLong())
        return when (spec.granularity) {
            Granularity.DAYS -> ChronoUnit.DAYS.between(spec.birthDate, end).toInt()
            Granularity.WEEKS -> ChronoUnit.DAYS.between(spec.birthDate, end).toInt() / DAYS_PER_WEEK
            Granularity.MONTHS -> spec.lifeSpanYears * MONTHS_PER_YEAR
            Granularity.YEARS -> spec.lifeSpanYears
        }
    }

    fun currentIndex(spec: LifeGridSpec): Int {
        if (spec.today.isBefore(spec.birthDate)) return BEFORE_BIRTH_INDEX
        val total = totalCells(spec)
        val idx = when (spec.granularity) {
            Granularity.DAYS ->
                ChronoUnit.DAYS.between(spec.birthDate, spec.today).toInt()
            Granularity.WEEKS ->
                ChronoUnit.DAYS.between(spec.birthDate, spec.today).toInt() / DAYS_PER_WEEK
            Granularity.MONTHS ->
                ChronoUnit.MONTHS.between(spec.birthDate, spec.today).toInt()
            Granularity.YEARS ->
                ChronoUnit.YEARS.between(spec.birthDate, spec.today).toInt()
        }
        return if (idx >= total) total else idx
    }

    fun cellAt(spec: LifeGridSpec, index: Int): LifeGridCell {
        val total = totalCells(spec)
        require(index in 0 until total) { "index $index out of bounds 0..${total - 1}" }
        val (start, endExclusive) = when (spec.granularity) {
            Granularity.DAYS -> {
                val s = spec.birthDate.plusDays(index.toLong())
                s to s.plusDays(1)
            }
            Granularity.WEEKS -> {
                val s = spec.birthDate.plusDays(index.toLong() * DAYS_PER_WEEK)
                s to s.plusDays(DAYS_PER_WEEK.toLong())
            }
            Granularity.MONTHS -> {
                val s = spec.birthDate.plusMonths(index.toLong())
                s to s.plusMonths(1)
            }
            Granularity.YEARS -> {
                val s = spec.birthDate.plusYears(index.toLong())
                s to s.plusYears(1)
            }
        }
        val endInclusive = endExclusive.minusDays(1)
        return LifeGridCell(
            index = index,
            state = LivingRoundingPolicy.classify(start, endInclusive, spec.today),
            startDate = start,
            endDate = endInclusive,
        )
    }

    /**
     * Returns a packed Int array — 2 bits per cell:
     *   00 = PAST, 01 = PRESENT, 10 = FUTURE.
     * Use [unpackState] to read back.
     */
    fun stateBitset(spec: LifeGridSpec): IntArray {
        val total = totalCells(spec)
        val current = currentIndex(spec)
        val ints = (total * BITS_PER_CELL + Int.SIZE_BITS - 1) / Int.SIZE_BITS
        val out = IntArray(ints)
        // Mark PAST (00) is the default zero state, just need PRESENT(01) + FUTURE(10).
        for (i in 0 until total) {
            val code = when {
                current == BEFORE_BIRTH_INDEX -> FUTURE_CODE
                i < current -> PAST_CODE
                i == current -> PRESENT_CODE
                else -> FUTURE_CODE
            }
            if (code == PAST_CODE) continue
            val bitIndex = i * BITS_PER_CELL
            val arrIndex = bitIndex / Int.SIZE_BITS
            val shift = bitIndex % Int.SIZE_BITS
            out[arrIndex] = out[arrIndex] or (code shl shift)
        }
        return out
    }

    companion object {
        const val DAYS_PER_WEEK = 7
        const val MONTHS_PER_YEAR = 12
        const val BEFORE_BIRTH_INDEX = -1
        const val BITS_PER_CELL = 2
        const val PAST_CODE = 0
        const val PRESENT_CODE = 1
        const val FUTURE_CODE = 2

        fun unpackState(packed: IntArray, index: Int): CellState {
            val bitIndex = index * BITS_PER_CELL
            val arrIndex = bitIndex / Int.SIZE_BITS
            val shift = bitIndex % Int.SIZE_BITS
            val code = (packed[arrIndex] ushr shift) and 0b11
            return when (code) {
                PAST_CODE -> CellState.PAST
                PRESENT_CODE -> CellState.PRESENT
                else -> CellState.FUTURE
            }
        }
    }
}
