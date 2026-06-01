package com.burstlog.settings

/**
 * All user-customizable settings, loaded from DataStore.
 *
 * Each field has a sensible default so the app works the moment it is installed.
 */
data class AppSettings(
    /** Which trainer voice to use: MALE, FEMALE, WHISTLE, or NONE. */
    val voiceType: VoiceType = VoiceType.MALE,

    /**
     * Number of seconds remaining in the WORK interval at which the
     * countdown voice begins (counts down to 0). Default 10.
     * If set higher than the work duration itself, the engine clamps it.
     */
    val workCountdownStartSeconds: Int = 10,

    /**
     * Number of seconds remaining in the REST interval at which the
     * countdown voice begins. Default 3.
     */
    val restCountdownStartSeconds: Int = 3,

    /** Whether vibration plays at the start of each work/rest interval. */
    val vibrateOnIntervalStart: Boolean = true,

    /**
     * Vibrate during the final N seconds of the WORK interval.
     * 0 disables this. Default 3.
     */
    val workVibrationCountdownSeconds: Int = 3,

    /** Vibrate during the final N seconds of the REST interval. Default 3. */
    val restVibrationCountdownSeconds: Int = 3,

    /** Theme preference: DARK, LIGHT, or SYSTEM. */
    val themeMode: ThemeMode = ThemeMode.DARK
)

enum class VoiceType { MALE, FEMALE, WHISTLE, NONE }

enum class ThemeMode { DARK, LIGHT, SYSTEM }
