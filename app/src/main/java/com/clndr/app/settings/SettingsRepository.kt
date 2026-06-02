package com.clndr.app.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.clndr.core.designsystem.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "clndr_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val birthDate: Flow<LocalDate?> = context.dataStore.data.map { prefs ->
        prefs[KEY_BIRTH_EPOCH_DAY]?.let { LocalDate.ofEpochDay(it) }
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.FOLLOW_SYSTEM
    }

    val sunriseAutoEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SUNRISE_AUTO] ?: false
    }

    val sunriseLatitude: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[KEY_SUNRISE_LAT] ?: DEFAULT_LATITUDE
    }

    suspend fun setBirthDate(date: LocalDate?) {
        println("CLNDR_TEST: setBirthDate called with date=$date")
        context.dataStore.edit { prefs ->
            println("CLNDR_TEST: datastore edit block starting, current val = ${prefs[KEY_BIRTH_EPOCH_DAY]}")
            if (date == null) {
                prefs.remove(KEY_BIRTH_EPOCH_DAY)
            } else {
                prefs[KEY_BIRTH_EPOCH_DAY] = date.toEpochDay()
            }
            println("CLNDR_TEST: datastore edit block finished, new val = ${prefs[KEY_BIRTH_EPOCH_DAY]}")
        }
        println("CLNDR_TEST: datastore edit completed")
        runCatching {
            val sharedPrefs = context.getSharedPreferences("clndr_settings", Context.MODE_PRIVATE)
            sharedPrefs.edit().apply {
                if (date == null) {
                    remove("birth_epoch_day")
                } else {
                    putLong("birth_epoch_day", date.toEpochDay())
                }
                apply()
            }
        }
        runCatching {
            com.clndr.feature.widgets.shared.WidgetUpdater.updateAll(context)
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[KEY_THEME_MODE] = mode.name }
        runCatching {
            com.clndr.feature.widgets.shared.WidgetUpdater.updateAll(context)
        }
    }

    suspend fun setSunriseAuto(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_SUNRISE_AUTO] = enabled }
        runCatching {
            com.clndr.feature.widgets.shared.WidgetUpdater.updateAll(context)
        }
    }

    suspend fun setSunriseLatitude(lat: Double) {
        context.dataStore.edit { prefs -> prefs[KEY_SUNRISE_LAT] = lat }
    }

    companion object {
        private val KEY_BIRTH_EPOCH_DAY = longPreferencesKey("birth_epoch_day")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_SUNRISE_AUTO = booleanPreferencesKey("sunrise_auto")
        private val KEY_SUNRISE_LAT = doublePreferencesKey("sunrise_lat")
        private const val DEFAULT_LATITUDE = 30.0
    }
}
