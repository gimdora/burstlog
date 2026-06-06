package com.burstlog

import android.app.Application
import com.burstlog.audio.AudioCueManager
import com.burstlog.data.AppDatabase
import com.burstlog.data.BurstLogRepository
import com.burstlog.settings.SettingsRepository

/**
 * Custom [Application] subclass that acts as a simple dependency container.
 *
 * Holds singletons that should live for the entire process: the Room database,
 * the settings repository, and the audio cue manager.
 *
 * Activities access these via `application as BurstLogApplication`.
 */
class BurstLogApplication : Application() {

    lateinit var repository: BurstLogRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var audioCueManager: AudioCueManager
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = BurstLogRepository(db.workoutRecordDao(), db.presetDao())
        settingsRepository = SettingsRepository(this)
        audioCueManager = AudioCueManager(this)
    }

    override fun onTerminate() {
        // Note: onTerminate is only called on emulators in practice, but
        // releasing here is still good hygiene.
        audioCueManager.release()
        super.onTerminate()
    }

    /** Builds a ViewModelFactory wired with the singletons in this container. */
    fun viewModelFactory(): ViewModelFactory =
        ViewModelFactory(repository, settingsRepository, audioCueManager)
}
