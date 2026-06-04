package com.clndr.core.domain

import com.clndr.core.datetime.Granularity
import com.clndr.core.datetime.LifeGridCalculator
import com.clndr.core.domain.usecase.GetLifeGridUseCase
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class GetLifeGridUseCaseTest {

    @Test
    fun `snapshot exposes lived-plus-headroom totals and current index for years`() {
        val now = Instant.parse("2026-05-31T00:00:00Z")
        val clock = Clock.fixed(now, ZoneOffset.UTC)
        val useCase = GetLifeGridUseCase(LifeGridCalculator(clock), clock)
        val snapshot = useCase(LocalDate.of(2000, 1, 1), Granularity.YEARS)
        // No fixed lifespan: horizon is age (26) + HEADROOM_YEARS (15) = 41.
        assertThat(snapshot.totalCells).isEqualTo(26 + GetLifeGridUseCase.HEADROOM_YEARS)
        assertThat(snapshot.currentIndex).isEqualTo(26)
        assertThat(snapshot.granularity).isEqualTo(Granularity.YEARS)
    }
}
