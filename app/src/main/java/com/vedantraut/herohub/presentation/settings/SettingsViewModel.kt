package com.vedantraut.herohub.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vedantraut.herohub.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.LoadSettings -> loadSettings()
            is SettingsIntent.SetThemeMode -> {
                viewModelScope.launch {
                    settingsRepository.setThemeMode(intent.mode)
                    _state.update { it.copy(themeMode = intent.mode, statusMessage = "Theme updated to ${intent.mode.name}") }
                }
            }
            is SettingsIntent.ToggleDataSaver -> {
                viewModelScope.launch {
                    settingsRepository.setDataSaverEnabled(intent.enabled)
                    _state.update { it.copy(isDataSaver = intent.enabled) }
                }
            }
            is SettingsIntent.ToggleOfflineForced -> {
                viewModelScope.launch {
                    settingsRepository.setOfflineModeForced(intent.forced)
                    _state.update { it.copy(isOfflineForced = intent.forced) }
                }
            }
            is SettingsIntent.ShowClearCacheDialog -> {
                _state.update { it.copy(showClearCacheConfirm = intent.show) }
            }
            is SettingsIntent.ConfirmClearCache -> {
                viewModelScope.launch {
                    settingsRepository.clearCache()
                    val newSize = settingsRepository.getCacheSizeFormatted()
                    _state.update {
                        it.copy(
                            showClearCacheConfirm = false,
                            cacheSize = newSize,
                            statusMessage = "Image cache cleared successfully"
                        )
                    }
                }
            }
            is SettingsIntent.ShowResetDialog -> {
                _state.update { it.copy(showResetConfirm = intent.show) }
            }
            is SettingsIntent.ConfirmResetAll -> {
                viewModelScope.launch {
                    settingsRepository.resetAllSettings()
                    val newSize = settingsRepository.getCacheSizeFormatted()
                    _state.update {
                        it.copy(
                            showResetConfirm = false,
                            cacheSize = newSize,
                            statusMessage = "Preferences reset to default"
                        )
                    }
                }
            }
            is SettingsIntent.DismissStatusMessage -> {
                _state.update { it.copy(statusMessage = null) }
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            launch {
                settingsRepository.getThemeMode().collect { mode ->
                    _state.update { it.copy(themeMode = mode) }
                }
            }
            launch {
                settingsRepository.isDataSaverEnabled().collect { saver ->
                    _state.update { it.copy(isDataSaver = saver) }
                }
            }
            launch {
                settingsRepository.isOfflineModeForced().collect { offline ->
                    _state.update { it.copy(isOfflineForced = offline) }
                }
            }
            launch {
                val size = settingsRepository.getCacheSizeFormatted()
                _state.update { it.copy(cacheSize = size) }
            }
        }
    }
}
