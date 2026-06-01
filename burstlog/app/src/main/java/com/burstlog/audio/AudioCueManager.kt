package com.burstlog.audio

import android.content.Context
import android.content.res.Resources
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.burstlog.settings.VoiceType

/**
 * Plays audio cues and triggers vibration during interval workouts.
 *
 * SoundPool is used instead of MediaPlayer because we need very low latency
 * (cues fire at exact 1-second boundaries during a countdown) and we play
 * many short clips.
 *
 * Audio files are looked up by resource name from `res/raw`. If a clip is
 * missing (for example, before the user adds the AI-generated voice files),
 * the manager logs a warning and silently skips it. The app still functions.
 */
class AudioCueManager(private val context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4) // Enough to overlap a couple of voices if needed
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    // Cache of resource name -> SoundPool sound id
    private val loadedSounds = mutableMapOf<String, Int>()

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Plays the cue for the start of a WORK interval, using the given voice.
     * Skipped silently when [VoiceType.NONE].
     */
    fun playWorkStart(voice: VoiceType) {
        playByName(audioFileNameFor(voice, CueKind.WORK_START))
    }

    fun playRestStart(voice: VoiceType) {
        playByName(audioFileNameFor(voice, CueKind.REST_START))
    }

    fun playWorkoutComplete(voice: VoiceType) {
        playByName(audioFileNameFor(voice, CueKind.DONE))
    }

    /**
     * Plays the number cue (1..15) using the given voice.
     * For WHISTLE voice, plays a generic short whistle instead.
     */
    fun playNumber(voice: VoiceType, secondsRemaining: Int) {
        if (voice == VoiceType.NONE) return
        if (voice == VoiceType.WHISTLE) {
            // For whistle voice, play a single short whistle on every tick.
            playByName("whistle_short")
            return
        }
        val prefix = if (voice == VoiceType.FEMALE) "female" else "male"
        playByName("${prefix}_count_$secondsRemaining")
    }

    /** Short pulse vibration, typically used at the start of an interval. */
    fun vibrateShort() {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        v.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    /** Quick tick vibration used during the final-seconds countdown. */
    fun vibrateTick() {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        v.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    /** Long pulse vibration for the end of the workout. */
    fun vibrateLong() {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        v.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    /** Releases SoundPool resources. Should be called when the engine is no longer needed. */
    fun release() {
        soundPool.release()
        loadedSounds.clear()
    }

    // ------------------- Internals -------------------

    /**
     * Looks up the resource id by name and plays it. Lazily loads the sound
     * into SoundPool on first use.
     */
    private fun playByName(resourceName: String) {
        val resId = try {
            @Suppress("DiscouragedApi")
            context.resources.getIdentifier(resourceName, "raw", context.packageName)
        } catch (e: Resources.NotFoundException) {
            0
        }
        if (resId == 0) {
            Log.w("AudioCueManager", "Missing audio resource: $resourceName")
            return
        }
        val soundId = loadedSounds.getOrPut(resourceName) {
            soundPool.load(context, resId, 1)
        }
        // Small delay-tolerant play. SoundPool returns 0 if the sound is not yet loaded.
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    private enum class CueKind { WORK_START, REST_START, DONE }

    private fun audioFileNameFor(voice: VoiceType, kind: CueKind): String {
        val prefix = when (voice) {
            VoiceType.MALE -> "male"
            VoiceType.FEMALE -> "female"
            VoiceType.WHISTLE -> "whistle"
            VoiceType.NONE -> return ""
        }
        val suffix = when (kind) {
            CueKind.WORK_START -> "work"
            CueKind.REST_START -> "rest"
            CueKind.DONE -> "done"
        }
        return "${prefix}_$suffix"
    }
}
