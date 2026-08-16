package com.knownassurajit.clndr_widget.app.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.knownassurajit.clndr_widget.core.designsystem.theme.ThemeMode
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetSettings
import com.knownassurajit.clndr_widget.feature.widgets.shared.WidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    val widgetsFollowAppTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_WIDGET_FOLLOW_APP] ?: true
    }

    suspend fun setBirthDate(date: LocalDate?) {
        context.dataStore.edit { prefs ->
            if (date == null) {
                prefs.remove(KEY_BIRTH_EPOCH_DAY)
            } else {
                prefs[KEY_BIRTH_EPOCH_DAY] = date.toEpochDay()
            }
        }
        mirrorSidecarAndRefresh()
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[KEY_THEME_MODE] = mode.name }
        mirrorSidecarAndRefresh()
    }

    suspend fun setSunriseAuto(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_SUNRISE_AUTO] = enabled }
        mirrorSidecarAndRefresh()
    }

    suspend fun setSunriseLatitude(lat: Double) {
        context.dataStore.edit { prefs -> prefs[KEY_SUNRISE_LAT] = lat }
        mirrorSidecarAndRefresh()
    }

    suspend fun setWidgetsFollowAppTheme(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_WIDGET_FOLLOW_APP] = enabled }
        mirrorSidecarAndRefresh()
    }

    suspend fun mirrorSidecarAndRefresh() {
        val birth = birthDate.first()
        val theme = themeMode.first()
        val sun = sunriseAutoEnabled.first()
        val lat = sunriseLatitude.first()
        val follow = widgetsFollowAppTheme.first()
        runCatching {
            context.getSharedPreferences(WidgetSettings.PREFS, Context.MODE_PRIVATE).edit().apply {
                if (birth == null) remove(WidgetSettings.KEY_BIRTH) else putLong(WidgetSettings.KEY_BIRTH, birth.toEpochDay())
                putString(WidgetSettings.KEY_THEME_MODE, theme.name)
                putBoolean(WidgetSettings.KEY_SUNRISE_AUTO, sun)
                putLong(WidgetSettings.KEY_SUNRISE_LAT, java.lang.Double.doubleToRawLongBits(lat))
                putBoolean(WidgetSettings.KEY_WIDGET_FOLLOW_APP, follow)
                apply()
            }
        }
        runCatching { WidgetUpdater.updateAll(context) }
    }

    companion object {
        private val KEY_BIRTH_EPOCH_DAY = longPreferencesKey("birth_epoch_day")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_SUNRISE_AUTO = booleanPreferencesKey("sunrise_auto")
        private val KEY_SUNRISE_LAT = doublePreferencesKey("sunrise_lat")
        private val KEY_WIDGET_FOLLOW_APP = booleanPreferencesKey("widget_follow_app")
        private const val DEFAULT_LATITUDE = 30.0
    }
}
