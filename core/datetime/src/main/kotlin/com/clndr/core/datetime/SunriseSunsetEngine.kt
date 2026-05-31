package com.clndr.core.datetime

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

data class SunTimes(val sunriseUtc: Instant, val sunsetUtc: Instant)

/**
 * Privacy-preserving sunrise/sunset estimator using NOAA's simplified formula.
 *
 * Latitude is supplied by the caller (defaults to 30°, a rough world-mean for inhabited
 * latitudes). Longitude is inferred from the zone's UTC offset on [date] (15° per hour).
 * Accuracy: within ~5–15 minutes for most populated latitudes — adequate for theme switching,
 * not for precise scheduling.
 */
class SunriseSunsetEngine {

    fun timesFor(
        date: LocalDate,
        zone: ZoneId,
        fallbackLatitude: Double = DEFAULT_LATITUDE,
    ): SunTimes {
        val offsetSeconds = zone.rules.getOffset(date.atStartOfDay()).totalSeconds
        val longitudeDeg = (offsetSeconds.toDouble() / SECONDS_PER_HOUR) * DEGREES_PER_HOUR
        val n = date.dayOfYear.toDouble()
        val lngHour = longitudeDeg / DEGREES_PER_HOUR

        val sunrise = solarEvent(date, n, lngHour, fallbackLatitude, rising = true)
        val sunset = solarEvent(date, n, lngHour, fallbackLatitude, rising = false)
        return SunTimes(sunrise, sunset)
    }

    fun isDaylight(
        at: Instant,
        zone: ZoneId,
        fallbackLatitude: Double = DEFAULT_LATITUDE,
    ): Boolean {
        val zdt = ZonedDateTime.ofInstant(at, zone)
        val today = zdt.toLocalDate()
        val (sunrise, sunset) = timesFor(today, zone, fallbackLatitude)
        return at.isAfter(sunrise) && at.isBefore(sunset)
    }

    fun nextTransition(
        after: Instant,
        zone: ZoneId,
        fallbackLatitude: Double = DEFAULT_LATITUDE,
    ): Instant {
        val today = ZonedDateTime.ofInstant(after, zone).toLocalDate()
        val todayTimes = timesFor(today, zone, fallbackLatitude)
        return when {
            after.isBefore(todayTimes.sunriseUtc) -> todayTimes.sunriseUtc
            after.isBefore(todayTimes.sunsetUtc) -> todayTimes.sunsetUtc
            else -> timesFor(today.plusDays(1), zone, fallbackLatitude).sunriseUtc
        }
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod")
    private fun solarEvent(
        date: LocalDate,
        dayOfYear: Double,
        lngHour: Double,
        latitudeDeg: Double,
        rising: Boolean,
    ): Instant {
        val t = if (rising) {
            dayOfYear + ((HOURS_PER_DAY / 4.0 - lngHour) / HOURS_PER_DAY)
        } else {
            dayOfYear + ((3.0 * HOURS_PER_DAY / 4.0 - lngHour) / HOURS_PER_DAY)
        }

        val m = MEAN_ANOMALY_M1 * t - MEAN_ANOMALY_M2

        var l = m + EQUATION_C1 * sin(m.toRadians()) + EQUATION_C2 * sin(2.0 * m.toRadians()) + EQUATION_C3
        l = normalizeDegrees(l)

        var ra = atan2Deg(EQUATION_RA_TAN * sin(l.toRadians()), cos(l.toRadians()))
        ra = normalizeDegrees(ra)
        val lQuadrant = floor(l / QUADRANT_DEG) * QUADRANT_DEG
        val raQuadrant = floor(ra / QUADRANT_DEG) * QUADRANT_DEG
        ra += lQuadrant - raQuadrant
        ra /= DEGREES_PER_HOUR

        val sinDec = SOLAR_DECLINATION_COEFF * sin(l.toRadians())
        val cosDec = cos(asin(sinDec))

        val zenithCos = cos(OFFICIAL_ZENITH_DEG.toRadians())
        val cosH = (zenithCos - sinDec * sin(latitudeDeg.toRadians())) /
            (cosDec * cos(latitudeDeg.toRadians()))
        val cosHClamped = cosH.coerceIn(-1.0, 1.0)
        val h = if (rising) {
            HOURS_PER_DAY - acos(cosHClamped).toDegrees() / DEGREES_PER_HOUR
        } else {
            acos(cosHClamped).toDegrees() / DEGREES_PER_HOUR
        }

        val t2 = h + ra - LOCAL_TIME_OFFSET_FACTOR * t - LOCAL_TIME_CONSTANT

        var utHours = t2 - lngHour
        utHours = ((utHours % HOURS_PER_DAY) + HOURS_PER_DAY) % HOURS_PER_DAY

        val whole = utHours.toLong()
        val minutes = ((utHours - whole) * MINUTES_PER_HOUR).toLong()
        val seconds = ((utHours - whole - minutes / MINUTES_PER_HOUR.toDouble()) * SECONDS_PER_MINUTE_D).toLong()
        return ZonedDateTime.of(
            date.year, date.monthValue, date.dayOfMonth,
            whole.toInt() % HOURS_PER_DAY_INT,
            minutes.toInt() % MINUTES_PER_HOUR_INT,
            seconds.toInt() % MINUTES_PER_HOUR_INT,
            0,
            ZoneOffset.UTC,
        ).toInstant()
    }

    private fun Double.toRadians(): Double = this * PI / DEGREES_HALF
    private fun Double.toDegrees(): Double = this * DEGREES_HALF / PI
    private fun atan2Deg(y: Double, x: Double): Double {
        val deg = atan2(y, x).toDegrees()
        return if (deg < 0) deg + FULL_CIRCLE else deg
    }
    private fun normalizeDegrees(d: Double): Double {
        var v = d
        while (v < 0) v += FULL_CIRCLE
        while (v >= FULL_CIRCLE) v -= FULL_CIRCLE
        return v
    }

    companion object {
        const val DEFAULT_LATITUDE = 30.0
        private const val SECONDS_PER_HOUR = 3_600
        private const val DEGREES_PER_HOUR = 15.0
        private const val HOURS_PER_DAY = 24.0
        private const val HOURS_PER_DAY_INT = 24
        private const val MINUTES_PER_HOUR = 60.0
        private const val MINUTES_PER_HOUR_INT = 60
        private const val SECONDS_PER_MINUTE_D = 60.0
        private const val FULL_CIRCLE = 360.0
        private const val DEGREES_HALF = 180.0
        private const val QUADRANT_DEG = 90.0
        private const val MEAN_ANOMALY_M1 = 0.9856
        private const val MEAN_ANOMALY_M2 = 3.289
        private const val EQUATION_C1 = 1.916
        private const val EQUATION_C2 = 0.020
        private const val EQUATION_C3 = 282.634
        private const val EQUATION_RA_TAN = 0.91764
        private const val SOLAR_DECLINATION_COEFF = 0.39782
        private const val OFFICIAL_ZENITH_DEG = 90.833
        private const val LOCAL_TIME_OFFSET_FACTOR = 0.06571
        private const val LOCAL_TIME_CONSTANT = 6.622
    }
}
