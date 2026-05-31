package com.clndr.core.datetime

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale

data class ProgressBuckets(
    val eraPct: Double,
    val decadePct: Double,
    val yearPct: Double,
    val monthPct: Double,
    val weekPct: Double,
    val dayPct: Double,
) {
    val eraRemaining: Double get() = 1.0 - eraPct
    val decadeRemaining: Double get() = 1.0 - decadePct
    val yearRemaining: Double get() = 1.0 - yearPct
    val monthRemaining: Double get() = 1.0 - monthPct
    val weekRemaining: Double get() = 1.0 - weekPct
    val dayRemaining: Double get() = 1.0 - dayPct

    companion object {
        val EMPTY = ProgressBuckets(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }
}

class ProgressEngine(
    private val clock: Clock = Clock.systemDefaultZone(),
    private val lifeSpanYears: Int = LifeGridSpec.DEFAULT_LIFESPAN_YEARS,
) {

    fun compute(
        now: Instant = clock.instant(),
        zone: ZoneId = clock.zone,
        birthDate: LocalDate?,
    ): ProgressBuckets {
        val zdt = ZonedDateTime.ofInstant(now, zone)
        return ProgressBuckets(
            eraPct = eraPct(zdt, birthDate),
            decadePct = decadePct(zdt, birthDate),
            yearPct = yearPct(zdt),
            monthPct = monthPct(zdt),
            weekPct = weekPct(zdt),
            dayPct = dayPct(zdt),
        )
    }

    fun observe(zone: ZoneId, birthDate: LocalDate?): Flow<ProgressBuckets> = flow {
        while (true) {
            val now = clock.instant()
            emit(compute(now, zone, birthDate))
            val ms = now.toEpochMilli()
            val sleep = MILLIS_PER_SECOND - (ms % MILLIS_PER_SECOND)
            delay(sleep)
        }
    }.flowOn(Dispatchers.Default)

    private fun eraPct(now: ZonedDateTime, birthDate: LocalDate?): Double {
        if (birthDate == null) return 0.0
        val birthStart = birthDate.atStartOfDay(now.zone)
        val end = birthStart.plusYears(lifeSpanYears.toLong())
        return clampPct(birthStart, now, end)
    }

    private fun decadePct(now: ZonedDateTime, birthDate: LocalDate?): Double {
        val today = now.toLocalDate()
        val decadeStart: ZonedDateTime
        val decadeEnd: ZonedDateTime
        if (birthDate != null) {
            val yearsLived = ChronoUnit.YEARS.between(birthDate, today).toInt()
            val decadeIndex = yearsLived / YEARS_PER_DECADE
            decadeStart = birthDate.plusYears((decadeIndex * YEARS_PER_DECADE).toLong()).atStartOfDay(now.zone)
            decadeEnd = decadeStart.plusYears(YEARS_PER_DECADE.toLong())
        } else {
            val yr = today.year
            val decadeStartYear = yr - (yr % YEARS_PER_DECADE)
            decadeStart = LocalDate.of(decadeStartYear, 1, 1).atStartOfDay(now.zone)
            decadeEnd = decadeStart.plusYears(YEARS_PER_DECADE.toLong())
        }
        return clampPct(decadeStart, now, decadeEnd)
    }

    private fun yearPct(now: ZonedDateTime): Double {
        val start = LocalDate.of(now.year, 1, 1).atStartOfDay(now.zone)
        val end = start.plusYears(1)
        return clampPct(start, now, end)
    }

    private fun monthPct(now: ZonedDateTime): Double {
        val ym = YearMonth.from(now)
        val start = ym.atDay(1).atStartOfDay(now.zone)
        val end = start.plusMonths(1)
        return clampPct(start, now, end)
    }

    private fun weekPct(now: ZonedDateTime): Double {
        val firstDay = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val today = now.toLocalDate()
        val daysFromStart = ((today.dayOfWeek.value - firstDay.value) + DAYS_PER_WEEK) % DAYS_PER_WEEK
        val start = today.minusDays(daysFromStart.toLong()).atStartOfDay(now.zone)
        val end = start.plusDays(DAYS_PER_WEEK.toLong())
        return clampPct(start, now, end)
    }

    private fun dayPct(now: ZonedDateTime): Double {
        val start = now.toLocalDate().atStartOfDay(now.zone)
        val end = start.plusDays(1)
        return clampPct(start, now, end)
    }

    private fun clampPct(start: ZonedDateTime, now: ZonedDateTime, end: ZonedDateTime): Double {
        val totalMs = end.toInstant().toEpochMilli() - start.toInstant().toEpochMilli()
        if (totalMs <= 0L) return 0.0
        val elapsedMs = now.toInstant().toEpochMilli() - start.toInstant().toEpochMilli()
        return (elapsedMs.toDouble() / totalMs.toDouble()).coerceIn(0.0, 1.0)
    }

    companion object {
        private const val MILLIS_PER_SECOND = 1_000L
        private const val YEARS_PER_DECADE = 10
        private const val DAYS_PER_WEEK = 7
    }
}
