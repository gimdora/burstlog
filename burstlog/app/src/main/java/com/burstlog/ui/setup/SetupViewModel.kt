package com.burstlog.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burstlog.data.BurstLogRepository
import com.burstlog.data.Preset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * State for the Setup screen where the user configures a workout
 * before starting the timer.
 */
data class SetupUiState(
    val name: String = "My Workout",
    val workSeconds: Int = 20,
    val restSeconds: Int = 10,
    val totalRounds: Int = 8
)

class SetupViewModel(
    private val repository: BurstLogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SetupUiState())
    val state: StateFlow<SetupUiState> = _state.asStateFlow()

    fun setName(value: String) {
        _state.value = _state.value.copy(name = value.take(40))
    }

    /** Coerces to a safe range to avoid 0-second intervals or huge numbers. */
    fun setWorkSeconds(value: Int) {
        _state.value = _state.value.copy(workSeconds = value.coerceIn(5, 600))
    }

    fun setRestSeconds(value: Int) {
        _state.value = _state.value.copy(restSeconds = value.coerceIn(0, 600))
    }

    fun setTotalRounds(value: Int) {
        _state.value = _state.value.copy(totalRounds = value.coerceIn(1, 50))
    }

    /** Loads an existing preset into the form (e.g., user tapped it on Home). */
    fun loadFromPreset(preset: Preset) {
        _state.value = SetupUiState(
            name = preset.name,
            workSeconds = preset.workSeconds,
            restSeconds = preset.restSeconds,
            totalRounds = preset.totalRounds
        )
    }

    /** Persists the current configuration as a reusable preset. */
    fun saveAsPreset() {
        val s = _state.value
        viewModelScope.launch {
            repository.insertPreset(
                Preset(
                    name = s.name,
                    workSeconds = s.workSeconds,
                    restSeconds = s.restSeconds,
                    totalRounds = s.totalRounds,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
        }
    }
}
