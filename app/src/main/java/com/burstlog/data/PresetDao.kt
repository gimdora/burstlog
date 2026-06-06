package com.burstlog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [Preset] entries.
 *
 * Presets are sorted by most recently created first so users see their newest
 * saved configurations at the top of the list.
 */
@Dao
interface PresetDao {

    @Query("SELECT * FROM presets ORDER BY createdAtMillis DESC")
    fun observeAll(): Flow<List<Preset>>

    @Query("SELECT * FROM presets WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Preset?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: Preset): Long

    @Delete
    suspend fun delete(preset: Preset)

    @Query("DELETE FROM presets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
