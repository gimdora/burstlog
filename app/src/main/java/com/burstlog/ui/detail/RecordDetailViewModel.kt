package com.burstlog.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burstlog.data.BurstLogRepository
import com.burstlog.data.WorkoutRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Record Detail screen. Loads a single record by id
 * and supports deletion.
 */
class RecordDetailViewModel(
    private val repository: BurstLogRepository
) : ViewModel() {

    private val _record = MutableStateFlow<WorkoutRecord?>(null)
    val record: StateFlow<WorkoutRecord?> = _record.asStateFlow()

    fun load(id: Long) {
        viewModelScope.launch {
            _record.value = repository.getWorkoutById(id)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val current = _record.value ?: return
        viewModelScope.launch {
            repository.deleteWorkout(current)
            onDeleted()
        }
    }
}
