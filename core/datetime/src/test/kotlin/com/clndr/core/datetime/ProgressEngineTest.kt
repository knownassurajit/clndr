package com.clndr.core.datetime

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class ProgressEngineTest {

    @Test
    fun `mid-day in a non-leap year reports yearPct around 0_5`() {
        val now = Instant.parse("2026-07-02T12:00:00Z")
        val engine = ProgressEngine(Clock.fixed(now, ZoneOffset.UTC))
        val buckets = engine.compute(birthDate = LocalDate.of(2000, 1, 1))
        assertThat(buckets.yearPct).isWithin(0.01).of(0.5)
    }

    @Test
    fun `start of year reports yearPct near zero`() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val engine = ProgressEngine(Clock.fixed(now, ZoneOffset.UTC))
        val buckets = engine.compute(birthDate = LocalDate.of(2000, 1, 1))
        assertThat(buckets.yearPct).isLessThan(0.001)
    }

    @Test
    fun `noon reports dayPct of 0_5`() {
        val now = Instant.parse("2026-07-02T12:00:00Z")
        val engine = ProgressEngine(Clock.fixed(now, ZoneOffset.UTC))
        val buckets = engine.compute(birthDate = null)
        assertThat(buckets.dayPct).isWithin(0.001).of(0.5)
    }

    @Test
    fun `era percentage is zero when birthDate is null`() {
        val now = Instant.parse("2026-07-02T12:00:00Z")
        val engine = ProgressEngine(Clock.fixed(now, ZoneOffset.UTC))
        assertThat(engine.compute(birthDate = null).eraPct).isEqualTo(0.0)
    }

    @Test
    fun `era percentage around quarter life for 26 of 110 years`() {
        val now = Instant.parse("2026-06-01T12:00:00Z")
        val engine = ProgressEngine(Clock.fixed(now, ZoneOffset.UTC))
        val pct = engine.compute(birthDate = LocalDate.of(2000, 6, 1)).eraPct
        assertThat(pct).isWithin(0.005).of(26.0 / 110.0)
    }

    @Test
    fun `decade pct anchors to birthdate when provided`() {
        val now = Instant.parse("2026-06-01T12:00:00Z")
        val engine = ProgressEngine(Clock.fixed(now, ZoneOffset.UTC))
        // Birth 2000-06-01, now 2026-06-01 = 26 yrs in. Decade index 2, decade started 2020-06-01.
        // 6 years into a 10-year decade ≈ 0.6.
        val pct = engine.compute(birthDate = LocalDate.of(2000, 6, 1)).decadePct
        assertThat(pct).isWithin(0.01).of(0.6)
    }

    @Test
    fun `progress buckets remaining sums to 1`() {
        val now = Instant.parse("2026-07-02T12:00:00Z")
        val engine = ProgressEngine(Clock.fixed(now, ZoneOffset.UTC))
        val buckets = engine.compute(birthDate = LocalDate.of(2000, 1, 1))
        assertThat(buckets.yearPct + buckets.yearRemaining).isWithin(1e-9).of(1.0)
        assertThat(buckets.dayPct + buckets.dayRemaining).isWithin(1e-9).of(1.0)
    }
}
