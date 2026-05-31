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
        context.dataStore.edit { prefs ->
            if (date == null) prefs.remove(KEY_BIRTH_EPOCH_DAY)
            else prefs[KEY_BIRTH_EPOCH_DAY] = date.toEpochDay()
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[KEY_THEME_MODE] = mode.name }
    }

    suspend fun setSunriseAuto(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_SUNRISE_AUTO] = enabled }
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
