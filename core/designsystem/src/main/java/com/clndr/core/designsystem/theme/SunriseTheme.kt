package com.clndr.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.clndr.core.datetime.SunriseSunsetEngine
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId

/**
 * Tracks whether the sun is currently above the horizon for the given [zone] and [latitude].
 * Recomputes the next horizon transition and reschedules accordingly.
 */
@Composable
fun rememberSunIsUp(
    zone: ZoneId = ZoneId.systemDefault(),
    latitude: Double = SunriseSunsetEngine.DEFAULT_LATITUDE,
    engine: SunriseSunsetEngine = remember { SunriseSunsetEngine() },
): State<Boolean> {
    val state = remember { mutableStateOf(engine.isDaylight(Instant.now(), zone, latitude)) }

    LaunchedEffect(zone, latitude, engine) {
        while (true) {
            val now = Instant.now()
            state.value = engine.isDaylight(now, zone, latitude)
            val next = engine.nextTransition(now, zone, latitude)
            val sleepMs = (next.toEpochMilli() - now.toEpochMilli()).coerceAtLeast(MIN_RESCHEDULE_MS)
            delay(sleepMs)
        }
    }

    return state
}

private const val MIN_RESCHEDULE_MS = 60_000L
