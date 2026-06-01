package com.burstlog.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "burstlog_settings")

/**
 * Persists user settings using Jetpack DataStore (Preferences).
 *
 * DataStore is the modern replacement for SharedPreferences. It uses
 * Kotlin coroutines and Flow, which fits well with the rest of the app.
 */
class SettingsRepository(private val context: Context) {

    /** Reactive stream of the current settings. Emits a new value on every change. */
    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            voiceType = VoiceType.valueOf(prefs[KEY_VOICE] ?: VoiceType.MALE.name),
            workCountdownStartSeconds = prefs[KEY_WORK_COUNTDOWN] ?: 10,
            restCountdownStartSeconds = prefs[KEY_REST_COUNTDOWN] ?: 3,
            vibrateOnIntervalStart = prefs[KEY_VIB_START] ?: true,
            workVibrationCountdownSeconds = prefs[KEY_WORK_VIB] ?: 3,
            restVibrationCountdownSeconds = prefs[KEY_REST_VIB] ?: 3,
            themeMode = ThemeMode.valueOf(prefs[KEY_THEME] ?: ThemeMode.DARK.name)
        )
    }

    suspend fun setVoiceType(value: VoiceType) {
        context.dataStore.edit { it[KEY_VOICE] = value.name }
    }

    suspend fun setWorkCountdownStart(value: Int) {
        context.dataStore.edit { it[KEY_WORK_COUNTDOWN] = value.coerceIn(0, 60) }
    }

    suspend fun setRestCountdownStart(value: Int) {
        context.dataStore.edit { it[KEY_REST_COUNTDOWN] = value.coerceIn(0, 60) }
    }

    suspend fun setVibrateOnIntervalStart(value: Boolean) {
        context.dataStore.edit { it[KEY_VIB_START] = value }
    }

    suspend fun setWorkVibrationCountdown(value: Int) {
        context.dataStore.edit { it[KEY_WORK_VIB] = value.coerceIn(0, 60) }
    }

    suspend fun setRestVibrationCountdown(value: Int) {
        context.dataStore.edit { it[KEY_REST_VIB] = value.coerceIn(0, 60) }
    }

    suspend fun setThemeMode(value: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME] = value.name }
    }

    companion object {
        private val KEY_VOICE = stringPreferencesKey("voice_type")
        private val KEY_WORK_COUNTDOWN = intPreferencesKey("work_countdown_start")
        private val KEY_REST_COUNTDOWN = intPreferencesKey("rest_countdown_start")
        private val KEY_VIB_START = booleanPreferencesKey("vibrate_on_interval_start")
        private val KEY_WORK_VIB = intPreferencesKey("work_vibration_countdown")
        private val KEY_REST_VIB = intPreferencesKey("rest_vibration_countdown")
        private val KEY_THEME = stringPreferencesKey("theme_mode")
    }
}
