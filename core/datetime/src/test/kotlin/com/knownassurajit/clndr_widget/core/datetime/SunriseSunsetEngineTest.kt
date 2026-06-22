package com.knownassurajit.clndr_widget.core.datetime

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class SunriseSunsetEngineTest {

    private val engine = SunriseSunsetEngine()

    @Test
    fun `sunrise is before sunset`() {
        val times = engine.timesFor(LocalDate.of(2026, 6, 21), ZoneId.of("UTC"), fallbackLatitude = 30.0)
        assertThat(times.sunriseUtc.isBefore(times.sunsetUtc)).isTrue()
    }

    @Test
    fun `daylight at solar noon is true`() {
        val date = LocalDate.of(2026, 6, 21)
        val times = engine.timesFor(date, ZoneId.of("UTC"), fallbackLatitude = 30.0)
        val mid = Instant.ofEpochSecond(
            (times.sunriseUtc.epochSecond + times.sunsetUtc.epochSecond) / 2,
        )
        assertThat(engine.isDaylight(mid, ZoneId.of("UTC"), fallbackLatitude = 30.0)).isTrue()
    }

    @Test
    fun `nextTransition past sunset jumps to tomorrow sunrise`() {
        val zone = ZoneId.of("UTC")
        val date = LocalDate.of(2026, 6, 21)
        val times = engine.timesFor(date, zone, fallbackLatitude = 30.0)
        val after = times.sunsetUtc.plusSeconds(60)
        val next = engine.nextTransition(after, zone, fallbackLatitude = 30.0)
        val tomorrowSunrise = engine.timesFor(date.plusDays(1), zone, fallbackLatitude = 30.0).sunriseUtc
        assertThat(next).isEqualTo(tomorrowSunrise)
    }
}
