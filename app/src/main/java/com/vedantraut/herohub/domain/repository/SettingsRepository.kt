package com.vedantraut.herohub.domain.repository

import kotlinx.coroutines.flow.Flow

enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

interface SettingsRepository {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
    fun isDataSaverEnabled(): Flow<Boolean>
    suspend fun setDataSaverEnabled(enabled: Boolean)
    fun isOfflineModeForced(): Flow<Boolean>
    suspend fun setOfflineModeForced(forced: Boolean)
    suspend fun getCacheSizeFormatted(): String
    suspend fun clearCache()
    suspend fun resetAllSettings()
}
