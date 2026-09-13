package com.vedantraut.herohub.presentation.settings

import com.vedantraut.herohub.domain.repository.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isDataSaver: Boolean = false,
    val isOfflineForced: Boolean = false,
    val cacheSize: String = "Calculating...",
    val showClearCacheConfirm: Boolean = false,
    val showResetConfirm: Boolean = false,
    val appVersion: String = "1.0.0 (Phase 7)",
    val statusMessage: String? = null
)
