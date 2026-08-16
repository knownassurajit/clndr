package com.knownassurajit.clndr_widget.feature.widgets.shared

import android.content.Context
import com.knownassurajit.clndr_widget.core.datetime.SunriseSunsetEngine
import com.knownassurajit.clndr_widget.core.designsystem.theme.ThemeMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Reads the birthdate and appearance the user persisted. Widgets must not depend on
 * the :app module, so we read the SharedPreferences sidecar that [SettingsRepository]
 * mirrors from DataStore.
 */
object WidgetSettings {

    const val PREFS = "clndr_settings"
    const val KEY_BIRTH = "birth_epoch_day"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_SUNRISE_AUTO = "sunrise_auto"
    const val KEY_SUNRISE_LAT = "sunrise_lat"
    const val KEY_WIDGET_FOLLOW_APP = "widget_follow_app"

    fun readBirthDate(context: Context): LocalDate? = runCatching {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(KEY_BIRTH, -1L)
            .takeIf { it >= 0 }
            ?.let { LocalDate.ofEpochDay(it) }
    }.getOrNull()

    fun readThemeMode(context: Context): ThemeMode = runCatching {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val sunrise = prefs.getBoolean(KEY_SUNRISE_AUTO, false)
        if (sunrise) return@runCatching ThemeMode.SUNRISE_AUTO
        prefs.getString(KEY_THEME_MODE, null)
            ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.FOLLOW_SYSTEM
    }.getOrDefault(ThemeMode.FOLLOW_SYSTEM)

    fun readWidgetFollowApp(context: Context): Boolean = runCatching {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_WIDGET_FOLLOW_APP, true)
    }.getOrDefault(true)

    fun colorMode(context: Context): WidgetColorMode {
        if (!readWidgetFollowApp(context)) return WidgetColorMode.SYSTEM
        return when (readThemeMode(context)) {
            ThemeMode.FORCE_LIGHT -> WidgetColorMode.LIGHT
            ThemeMode.FORCE_DARK -> WidgetColorMode.DARK
            ThemeMode.FOLLOW_SYSTEM -> WidgetColorMode.SYSTEM
            ThemeMode.SUNRISE_AUTO -> {
                val lat = runCatching {
                    val bits = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                        .getLong(KEY_SUNRISE_LAT, java.lang.Double.doubleToRawLongBits(30.0))
                    java.lang.Double.longBitsToDouble(bits)
                }.getOrDefault(SunriseSunsetEngine.DEFAULT_LATITUDE)
                val sunUp = SunriseSunsetEngine().isDaylight(
                    Instant.now(),
                    ZoneId.systemDefault(),
                    lat,
                )
                if (sunUp) WidgetColorMode.LIGHT else WidgetColorMode.DARK
            }
        }
    }
}
