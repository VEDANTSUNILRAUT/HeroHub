package com.vedantraut.herohub.presentation.settings

import com.vedantraut.herohub.domain.repository.ThemeMode

sealed interface SettingsIntent {
    data object LoadSettings : SettingsIntent
    data class SetThemeMode(val mode: ThemeMode) : SettingsIntent
    data class ToggleDataSaver(val enabled: Boolean) : SettingsIntent
    data class ToggleOfflineForced(val forced: Boolean) : SettingsIntent
    data class ShowClearCacheDialog(val show: Boolean) : SettingsIntent
    data object ConfirmClearCache : SettingsIntent
    data class ShowResetDialog(val show: Boolean) : SettingsIntent
    data object ConfirmResetAll : SettingsIntent
    data object DismissStatusMessage : SettingsIntent
}
