package com.burstlog.timer

import com.burstlog.audio.AudioCueManager
import com.burstlog.settings.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Which part of the workout is currently active. */
enum class TimerPhase { IDLE, WORK, REST, DONE }

/**
 * Snapshot of the timer state at a given moment.
 *
 * @property phase Current phase (WORK / REST / DONE / IDLE).
 * @property secondsRemaining Seconds left in the current interval.
 * @property currentRound 1-indexed current round.
 * @property totalRounds Total planned rounds.
 * @property isPaused Whether the engine is paused.
 * @property elapsedTotalSeconds Total elapsed time of the session so far.
 */
data class TimerState(
    val phase: TimerPhase = TimerPhase.IDLE,
    val secondsRemaining: Int = 0,
    val currentRound: Int = 0,
    val totalRounds: Int = 0,
    val isPaused: Boolean = false,
    val elapsedTotalSeconds: Int = 0
)

/**
 * Configuration the engine needs to run a session.
 */
data class TimerConfig(
    val workSeconds: Int,
    val restSeconds: Int,
    val totalRounds: Int
)

/**
 * Drives the WORK / REST countdown loop for a single workout session.
 *
 * The engine runs inside a coroutine, ticking once per second. On each tick
 * it updates [state] and fires audio/vibration cues based on the current
 * [AppSettings] passed in at start time.
 *
 * A simple state machine:
 *   IDLE -> WORK -> REST -> WORK -> REST -> ... -> DONE
 *
 * After the last round's REST is skipped (the workout ends right after
 * the last WORK interval).
 */
class TimerEngine(
    private val scope: CoroutineScope,
    private val audio: AudioCueManager
) {
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var job: Job? = null
    private var settings: AppSettings = AppSettings()
    private var config: TimerConfig = TimerConfig(20, 10, 8)

    /**
     * Starts a new workout session. Cancels any previous session in flight.
     */
    fun start(config: TimerConfig, settings: AppSettings) {
        this.config = config
        this.settings = settings
        job?.cancel()
        _state.value = TimerState(
            phase = TimerPhase.WORK,
            secondsRemaining = config.workSeconds,
            currentRound = 1,
            totalRounds = config.totalRounds,
            isPaused = false,
            elapsedTotalSeconds = 0
        )
        // Cue for the very first WORK interval.
        fireIntervalStartCues(TimerPhase.WORK)
        job = scope.launch { run() }
    }

    fun pause() {
        _state.value = _state.value.copy(isPaused = true)
    }

    fun resume() {
        _state.value = _state.value.copy(isPaused = false)
    }

    /** Stops the session immediately and moves state to DONE. */
    fun stop() {
        job?.cancel()
        job = null
        _state.value = _state.value.copy(phase = TimerPhase.DONE, isPaused = false)
    }

    /**
     * The main loop. Ticks every second. Decrements seconds remaining,
     * transitions phases as needed, and fires audio cues.
     */
    private suspend fun run() {
        while (true) {
            delay(1000L)
            val current = _state.value
            if (current.isPaused) continue
            if (current.phase == TimerPhase.DONE) break

            // Fire countdown cues BEFORE decrementing, so the voice plays at the
            // moment when "X seconds remaining" is exactly true.
            fireCountdownCues(current)

            val nextSecondsRemaining = current.secondsRemaining - 1
            _state.value = current.copy(
                secondsRemaining = nextSecondsRemaining,
                elapsedTotalSeconds = current.elapsedTotalSeconds + 1
            )

            if (nextSecondsRemaining <= 0) {
                advancePhase()
                if (_state.value.phase == TimerPhase.DONE) break
            }
        }
    }

    /**
     * Moves to the next phase: WORK -> REST, REST -> WORK (next round), or DONE.
     */
    private fun advancePhase() {
        val current = _state.value
        when (current.phase) {
            TimerPhase.WORK -> {
                if (current.currentRound >= config.totalRounds) {
                    // No rest after the last round; workout is complete.
                    _state.value = current.copy(phase = TimerPhase.DONE)
                    audio.playWorkoutComplete(settings.voiceType)
                    audio.vibrateLong()
                } else {
                    _state.value = current.copy(
                        phase = TimerPhase.REST,
                        secondsRemaining = config.restSeconds
                    )
                    fireIntervalStartCues(TimerPhase.REST)
                }
            }
            TimerPhase.REST -> {
                _state.value = current.copy(
                    phase = TimerPhase.WORK,
                    secondsRemaining = config.workSeconds,
                    currentRound = current.currentRound + 1
                )
                fireIntervalStartCues(TimerPhase.WORK)
            }
            TimerPhase.IDLE, TimerPhase.DONE -> Unit // Should not happen here
        }
    }

    private fun fireIntervalStartCues(phase: TimerPhase) {
        when (phase) {
            TimerPhase.WORK -> audio.playWorkStart(settings.voiceType)
            TimerPhase.REST -> audio.playRestStart(settings.voiceType)
            else -> Unit
        }
        if (settings.vibrateOnIntervalStart) {
            audio.vibrateShort()
        }
    }

    /**
     * Plays the per-second voice and/or vibration cues during the final
     * seconds of WORK or REST, based on user settings.
     */
    private fun fireCountdownCues(current: TimerState) {
        val remaining = current.secondsRemaining
        // Skip cues on the very first tick (the "start" cue already played).
        if (remaining <= 0) return

        val voiceWindow: Int
        val vibrationWindow: Int
        when (current.phase) {
            TimerPhase.WORK -> {
                voiceWindow = settings.workCountdownStartSeconds
                vibrationWindow = settings.workVibrationCountdownSeconds
            }
            TimerPhase.REST -> {
                voiceWindow = settings.restCountdownStartSeconds
                vibrationWindow = settings.restVibrationCountdownSeconds
            }
            else -> return
        }

        // Voice: speak the current "remaining" number when inside the window.
        if (voiceWindow > 0 && remaining <= voiceWindow) {
            audio.playNumber(settings.voiceType, remaining)
        }
        // Vibration: tick during the last vibrationWindow seconds.
        if (vibrationWindow > 0 && remaining <= vibrationWindow) {
            audio.vibrateTick()
        }
    }
}
