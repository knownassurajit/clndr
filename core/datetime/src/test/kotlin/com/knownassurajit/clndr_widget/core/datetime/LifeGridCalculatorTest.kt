package com.knownassurajit.clndr_widget.core.datetime

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class LifeGridCalculatorTest {

    private val calc = LifeGridCalculator()

    @Test
    fun `years granularity total cells equals lifespan`() {
        val spec = LifeGridSpec(
            birthDate = LocalDate.of(2000, 1, 1),
            granularity = Granularity.YEARS,
            today = LocalDate.of(2026, 5, 31),
        )
        assertThat(calc.totalCells(spec)).isEqualTo(110)
    }

    @Test
    fun `months granularity is lifespan times 12`() {
        val spec = baseSpec(Granularity.MONTHS)
        assertThat(calc.totalCells(spec)).isEqualTo(110 * 12)
    }

    @Test
    fun `weeks granularity is roughly 5720`() {
        val spec = baseSpec(Granularity.WEEKS)
        assertThat(calc.totalCells(spec)).isIn(5_700..5_740)
    }

    @Test
    fun `days granularity is roughly 40150`() {
        val spec = baseSpec(Granularity.DAYS)
        assertThat(calc.totalCells(spec)).isIn(40_100..40_200)
    }

    @Test
    fun `current index for years equals years lived`() {
        val spec = LifeGridSpec(
            birthDate = LocalDate.of(2000, 5, 1),
            granularity = Granularity.YEARS,
            today = LocalDate.of(2026, 5, 31),
        )
        assertThat(calc.currentIndex(spec)).isEqualTo(26)
    }

    @Test
    fun `current index is negative when today is before birth`() {
        val spec = LifeGridSpec(
            birthDate = LocalDate.of(2030, 1, 1),
            granularity = Granularity.YEARS,
            today = LocalDate.of(2026, 5, 31),
        )
        assertThat(calc.currentIndex(spec)).isEqualTo(LifeGridCalculator.BEFORE_BIRTH_INDEX)
    }

    @Test
    fun `current index clamps when today is past lifespan`() {
        val spec = LifeGridSpec(
            birthDate = LocalDate.of(1800, 1, 1),
            granularity = Granularity.YEARS,
            today = LocalDate.of(2026, 5, 31),
        )
        assertThat(calc.currentIndex(spec)).isEqualTo(calc.totalCells(spec))
    }

    @Test
    fun `cell at first index spans first day-after-birth window`() {
        val spec = baseSpec(Granularity.DAYS)
        val cell = calc.cellAt(spec, 0)
        assertThat(cell.startDate).isEqualTo(spec.birthDate)
        assertThat(cell.endDate).isEqualTo(spec.birthDate)
    }

    @Test
    fun `cell at zero in weeks spans 7 days`() {
        val spec = baseSpec(Granularity.WEEKS)
        val cell = calc.cellAt(spec, 0)
        assertThat(cell.startDate).isEqualTo(spec.birthDate)
        assertThat(cell.endDate).isEqualTo(spec.birthDate.plusDays(6))
    }

    @Test
    fun `leap year Feb 29 birthday resolves to Feb 28 in non-leap years`() {
        val spec = LifeGridSpec(
            birthDate = LocalDate.of(2000, 2, 29),
            granularity = Granularity.YEARS,
            today = LocalDate.of(2026, 5, 31),
        )
        val cell = calc.cellAt(spec, 1)
        assertThat(cell.startDate).isEqualTo(LocalDate.of(2001, 2, 28))
    }

    @Test
    fun `bitset round-trips PAST PRESENT FUTURE`() {
        val spec = LifeGridSpec(
            birthDate = LocalDate.of(2000, 1, 1),
            granularity = Granularity.YEARS,
            today = LocalDate.of(2026, 5, 31),
        )
        val packed = calc.stateBitset(spec)
        val current = calc.currentIndex(spec)
        assertThat(LifeGridCalculator.unpackState(packed, 0)).isEqualTo(CellState.PAST)
        assertThat(LifeGridCalculator.unpackState(packed, current)).isEqualTo(CellState.PRESENT)
        assertThat(LifeGridCalculator.unpackState(packed, current + 1)).isEqualTo(CellState.FUTURE)
    }

    @Test
    fun `state classification edges`() {
        val today = LocalDate.of(2026, 5, 31)
        assertThat(LivingRoundingPolicy.classify(today.minusDays(10), today.minusDays(1), today))
            .isEqualTo(CellState.PAST)
        assertThat(LivingRoundingPolicy.classify(today, today, today)).isEqualTo(CellState.PRESENT)
        assertThat(LivingRoundingPolicy.classify(today.minusDays(1), today.plusDays(1), today))
            .isEqualTo(CellState.PRESENT)
        assertThat(LivingRoundingPolicy.classify(today.plusDays(1), today.plusDays(7), today))
            .isEqualTo(CellState.FUTURE)
    }

    private fun baseSpec(g: Granularity) = LifeGridSpec(
        birthDate = LocalDate.of(2000, 1, 1),
        granularity = g,
        today = LocalDate.of(2026, 5, 31),
    )
}
