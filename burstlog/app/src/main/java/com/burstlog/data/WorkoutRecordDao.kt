package com.burstlog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [WorkoutRecord] entries.
 *
 * Uses Flow so the UI automatically updates when new records are inserted
 * or deleted, without needing to manually refresh.
 */
@Dao
interface WorkoutRecordDao {

    /** Returns all workout records ordered by most recent first. */
    @Query("SELECT * FROM workout_records ORDER BY startedAtMillis DESC")
    fun observeAll(): Flow<List<WorkoutRecord>>

    /** Returns a single workout record by its id, or null if it does not exist. */
    @Query("SELECT * FROM workout_records WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WorkoutRecord?

    /**
     * Returns the sum of total seconds across all workouts started after
     * the given time. Used to compute "this week" statistics on the home screen.
     */
    @Query("SELECT COALESCE(SUM(totalDurationSeconds), 0) FROM workout_records WHERE startedAtMillis >= :sinceMillis")
    fun observeTotalSecondsSince(sinceMillis: Long): Flow<Int>

    /** Returns the count of workouts started after the given time. */
    @Query("SELECT COUNT(*) FROM workout_records WHERE startedAtMillis >= :sinceMillis")
    fun observeWorkoutCountSince(sinceMillis: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: WorkoutRecord): Long

    @Delete
    suspend fun delete(record: WorkoutRecord)

    @Query("DELETE FROM workout_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
