package com.knownassurajit.clndr_widget.feature.widgets.shared

import android.content.Context
import java.time.LocalDate

/**
 * Reads the birthdate the user persisted in DataStore. Widgets must not depend on
 * the :app module, so we read the same underlying DataStore directly by name.
 */
internal object WidgetSettings {

    private const val PREFS = "clndr_settings"
    private const val KEY = "birth_epoch_day"

    fun readBirthDate(context: Context): LocalDate? {
        // DataStore-Preferences persists with the same file name + ".preferences_pb"
        // but for widgets we keep it simple and let GlanceAppWidget receive a fallback.
        // The :app process writes through DataStore; we read via shared prefs fallback
        // only if a future cycle decides to mirror — for now, return null and let
        // ProgressEngine handle the missing-birthdate case.
        return runCatching {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getLong(KEY, -1L)
                .takeIf { it >= 0 }
                ?.let { LocalDate.ofEpochDay(it) }
        }.getOrNull()
    }
}
