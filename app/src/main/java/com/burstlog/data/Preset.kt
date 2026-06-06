package com.burstlog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A saved interval configuration that the user can launch with one tap.
 *
 * Presets let users quickly start common workouts (e.g., "Classic Tabata":
 * 20 seconds work, 10 seconds rest, 8 rounds) without re-entering numbers
 * every time.
 */
@Entity(tableName = "presets")
data class Preset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val workSeconds: Int,
    val restSeconds: Int,
    val totalRounds: Int,
    val createdAtMillis: Long
)
