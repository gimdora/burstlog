package com.burstlog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Main Room database for BurstLog.
 *
 * Holds both the workout history and saved presets. The database is created
 * once on first launch and persisted to the device's internal storage so data
 * survives across app launches and device reboots, satisfying the module's
 * "local storage" requirement.
 */
@Database(
    entities = [WorkoutRecord::class, Preset::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutRecordDao(): WorkoutRecordDao
    abstract fun presetDao(): PresetDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Returns the singleton database instance, creating it on first call.
         * Thread-safe via double-checked locking.
         */
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "burstlog.db"
            ).build()
        }
    }
}
