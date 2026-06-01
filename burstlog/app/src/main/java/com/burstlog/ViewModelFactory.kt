package com.burstlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.burstlog.audio.AudioCueManager
import com.burstlog.data.BurstLogRepository
import com.burstlog.settings.SettingsRepository
import com.burstlog.ui.detail.RecordDetailViewModel
import com.burstlog.ui.home.HomeViewModel
import com.burstlog.ui.settings.SettingsViewModel
import com.burstlog.ui.setup.SetupViewModel
import com.burstlog.ui.timer.TimerViewModel

/**
 * Manual dependency injection factory.
 *
 * The app does not use a DI framework like Hilt to keep dependencies simple
 * and the learning curve manageable. Every ViewModel is constructed here
 * with whatever it needs from the [BurstLogApplication] singleton container.
 */
class ViewModelFactory(
    private val repository: BurstLogRepository,
    private val settingsRepository: SettingsRepository,
    private val audioCueManager: AudioCueManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(repository) as T
            modelClass.isAssignableFrom(SetupViewModel::class.java) ->
                SetupViewModel(repository) as T
            modelClass.isAssignableFrom(TimerViewModel::class.java) ->
                TimerViewModel(repository, settingsRepository, audioCueManager) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settingsRepository) as T
            modelClass.isAssignableFrom(RecordDetailViewModel::class.java) ->
                RecordDetailViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
