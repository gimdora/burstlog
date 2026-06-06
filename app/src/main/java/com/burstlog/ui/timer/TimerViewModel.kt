package com.burstlog.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burstlog.audio.AudioCueManager
import com.burstlog.data.BurstLogRepository
import com.burstlog.data.WorkoutRecord
import com.burstlog.settings.AppSettings
import com.burstlog.settings.SettingsRepository
import com.burstlog.timer.TimerConfig
import com.burstlog.timer.TimerEngine
import com.burstlog.timer.TimerPhase
import com.burstlog.timer.TimerState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for the Timer screen.
 *
 * Owns a single [TimerEngine] instance and exposes its state as a Flow.
 * When the workout finishes, it inserts a [WorkoutRecord] into the database.
 */
class TimerViewModel(
    private val repository: BurstLogRepository,
    private val settingsRepository: SettingsRepository,
    audio: AudioCueManager
) : ViewModel() {

    private val engine = TimerEngine(viewModelScope, audio)
    val state: StateFlow<TimerState> = engine.state

    // Saved config so we can build the WorkoutRecord at the end.
    private var workoutName: String = ""
    private var configSnapshot: TimerConfig = TimerConfig(20, 10, 8)
    private var startedAtMillis: Long = 0L
    private var recordSaved: Boolean = false

    /**
     * Begins a new workout session. Loads the current settings before starting
     * so the engine fires the correct audio/vibration cues.
     */
    fun startWorkout(name: String, workSeconds: Int, restSeconds: Int, totalRounds: Int) {
        workoutName = name.ifBlank { "Workout" }
        configSnapshot = TimerConfig(workSeconds, restSeconds, totalRounds)
        startedAtMillis = System.currentTimeMillis()
        recordSaved = false

        viewModelScope.launch {
            val settings: AppSettings = settingsRepository.settings.first()
            engine.start(configSnapshot, settings)

            // Watch the engine until it reports DONE, then save the record.
            engine.state.collect { s ->
                if (s.phase == TimerPhase.DONE && !recordSaved) {
                    saveRecord(s)
                    recordSaved = true
                }
            }
        }
    }

    fun pause() = engine.pause()
    fun resume() = engine.resume()
    fun stop() = engine.stop()

    /**
     * Persists the completed (or stopped) session.
     * Records partial completion if the user stopped early.
     */
    private suspend fun saveRecord(finalState: TimerState) {
        val completedRounds = when {
            finalState.currentRound == 0 -> 0
            finalState.phase == TimerPhase.DONE && finalState.secondsRemaining <= 0 -> finalState.currentRound
            else -> (finalState.currentRound - 1).coerceAtLeast(0)
        }
        val record = WorkoutRecord(
            name = workoutName,
            workSeconds = configSnapshot.workSeconds,
            restSeconds = configSnapshot.restSeconds,
            totalRounds = configSnapshot.totalRounds,
            completedRounds = completedRounds,
            totalDurationSeconds = finalState.elapsedTotalSeconds,
            startedAtMillis = startedAtMillis
        )
        repository.insertWorkout(record)
    }
}
