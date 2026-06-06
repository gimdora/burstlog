package com.burstlog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A finished workout session that is saved to the database when the user
 * completes (or stops partway through) an interval session.
 *
 * @property id Auto-generated primary key.
 * @property name The user-chosen name for the workout (e.g., "Pushup Tabata").
 * @property workSeconds Length of each work interval in seconds.
 * @property restSeconds Length of each rest interval in seconds.
 * @property totalRounds The total number of rounds the user planned.
 * @property completedRounds The number of rounds the user actually finished.
 * @property totalDurationSeconds Total elapsed time of the workout in seconds.
 * @property startedAtMillis Unix epoch in milliseconds when the workout started.
 */
@Entity(tableName = "workout_records")
data class WorkoutRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val workSeconds: Int,
    val restSeconds: Int,
    val totalRounds: Int,
    val completedRounds: Int,
    val totalDurationSeconds: Int,
    val startedAtMillis: Long
)
