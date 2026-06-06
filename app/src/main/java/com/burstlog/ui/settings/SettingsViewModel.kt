package com.burstlog.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burstlog.settings.AppSettings
import com.burstlog.settings.SettingsRepository
import com.burstlog.settings.ThemeMode
import com.burstlog.settings.VoiceType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen. Each setter delegates to the repository.
 */
class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings()
    )

    fun setVoice(value: VoiceType) = viewModelScope.launch { repository.setVoiceType(value) }
    fun setWorkCountdownStart(v: Int) = viewModelScope.launch { repository.setWorkCountdownStart(v) }
    fun setRestCountdownStart(v: Int) = viewModelScope.launch { repository.setRestCountdownStart(v) }
    fun setVibrateOnStart(v: Boolean) = viewModelScope.launch { repository.setVibrateOnIntervalStart(v) }
    fun setWorkVibCountdown(v: Int) = viewModelScope.launch { repository.setWorkVibrationCountdown(v) }
    fun setRestVibCountdown(v: Int) = viewModelScope.launch { repository.setRestVibrationCountdown(v) }
    fun setTheme(v: ThemeMode) = viewModelScope.launch { repository.setThemeMode(v) }
}
