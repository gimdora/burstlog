package com.burstlog.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

/**
 * Repository for workout history and presets.
 *
 * The repository pattern is used so the rest of the app does not depend
 * directly on Room. Later, if the data source changed (for example to a
 * cloud database), only this class would need to change.
 */
class BurstLogRepository(
    private val workoutRecordDao: WorkoutRecordDao,
    private val presetDao: PresetDao
) {

    // ------------------- Workout records -------------------

    fun observeAllWorkouts(): Flow<List<WorkoutRecord>> = workoutRecordDao.observeAll()

    /**
     * Observes the total seconds of all workouts started since Monday 00:00:00
     * of the current week. Used for the "this week" stats card.
     */
    fun observeThisWeekSeconds(): Flow<Int> =
        workoutRecordDao.observeTotalSecondsSince(startOfThisWeekMillis())

    fun observeThisWeekCount(): Flow<Int> =
        workoutRecordDao.observeWorkoutCountSince(startOfThisWeekMillis())

    suspend fun getWorkoutById(id: Long): WorkoutRecord? = workoutRecordDao.getById(id)

    suspend fun insertWorkout(record: WorkoutRecord): Long = workoutRecordDao.insert(record)

    suspend fun deleteWorkout(record: WorkoutRecord) = workoutRecordDao.delete(record)

    suspend fun deleteWorkoutById(id: Long) = workoutRecordDao.deleteById(id)

    // ------------------- Presets -------------------

    fun observeAllPresets(): Flow<List<Preset>> = presetDao.observeAll()

    suspend fun getPresetById(id: Long): Preset? = presetDao.getById(id)

    suspend fun insertPreset(preset: Preset): Long = presetDao.insert(preset)

    suspend fun deletePreset(preset: Preset) = presetDao.delete(preset)

    // ------------------- Helpers -------------------

    /** Returns the timestamp in millis for Monday 00:00:00 of the current week. */
    private fun startOfThisWeekMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        // Move to Monday of the current week
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val offsetFromMonday = when (dayOfWeek) {
            Calendar.SUNDAY -> -6
            else -> Calendar.MONDAY - dayOfWeek
        }
        cal.add(Calendar.DAY_OF_MONTH, offsetFromMonday)
        return cal.timeInMillis
    }
}
