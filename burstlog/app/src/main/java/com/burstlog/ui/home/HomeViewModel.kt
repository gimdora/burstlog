package com.burstlog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burstlog.data.BurstLogRepository
import com.burstlog.data.Preset
import com.burstlog.data.WorkoutRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * State exposed to the Home screen.
 *
 * Combines the workout history, the preset list, and weekly stats so the
 * screen can render all sections from a single Flow.
 */
data class HomeUiState(
    val records: List<WorkoutRecord> = emptyList(),
    val presets: List<Preset> = emptyList(),
    val thisWeekSeconds: Int = 0,
    val thisWeekCount: Int = 0
)

class HomeViewModel(
    private val repository: BurstLogRepository
) : ViewModel() {

    val records: StateFlow<List<WorkoutRecord>> = repository.observeAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val presets: StateFlow<List<Preset>> = repository.observeAllPresets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val thisWeekSeconds: StateFlow<Int> = repository.observeThisWeekSeconds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val thisWeekCount: StateFlow<Int> = repository.observeThisWeekCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Deletes a workout record by id. Called when the user swipes one away. */
    fun deleteRecord(id: Long) {
        viewModelScope.launch { repository.deleteWorkoutById(id) }
    }

    /** Deletes a saved preset. Called from the preset row's overflow menu. */
    fun deletePreset(preset: Preset) {
        viewModelScope.launch { repository.deletePreset(preset) }
    }
}
