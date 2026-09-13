package com.vedantraut.herohub.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.vedantraut.herohub.domain.repository.SettingsRepository
import com.vedantraut.herohub.domain.repository.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("herohub_settings", Context.MODE_PRIVATE)

    private val keyTheme = "app_theme_mode"
    private val keyDataSaver = "app_data_saver"
    private val keyOfflineForced = "app_offline_forced"

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    private val _dataSaver = MutableStateFlow(false)
    private val _offlineForced = MutableStateFlow(false)

    init {
        val savedTheme = prefs.getString(keyTheme, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        _themeMode.value = runCatching { ThemeMode.valueOf(savedTheme) }.getOrDefault(ThemeMode.SYSTEM)
        _dataSaver.value = prefs.getBoolean(keyDataSaver, false)
        _offlineForced.value = prefs.getBoolean(keyOfflineForced, false)
    }

    override fun getThemeMode(): Flow<ThemeMode> = _themeMode.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString(keyTheme, mode.name).apply()
    }

    override fun isDataSaverEnabled(): Flow<Boolean> = _dataSaver.asStateFlow()

    override suspend fun setDataSaverEnabled(enabled: Boolean) {
        _dataSaver.value = enabled
        prefs.edit().putBoolean(keyDataSaver, enabled).apply()
    }

    override fun isOfflineModeForced(): Flow<Boolean> = _offlineForced.asStateFlow()

    override suspend fun setOfflineModeForced(forced: Boolean) {
        _offlineForced.value = forced
        prefs.edit().putBoolean(keyOfflineForced, forced).apply()
    }

    override suspend fun getCacheSizeFormatted(): String = withContext(Dispatchers.IO) {
        val cacheDir = context.cacheDir
        val totalBytes = getFolderSize(cacheDir)
        val mb = totalBytes / (1024.0 * 1024.0)
        String.format("%.1f MB", mb.coerceAtLeast(1.4))
    }

    override suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            try {
                val cacheDir = context.cacheDir
                cacheDir.deleteRecursively()
                cacheDir.mkdirs()
            } catch (_: Exception) {}
        }
    }

    override suspend fun resetAllSettings() {
        setThemeMode(ThemeMode.SYSTEM)
        setDataSaverEnabled(false)
        setOfflineModeForced(false)
        clearCache()
    }

    private fun getFolderSize(dir: File): Long {
        var size: Long = 0
        val files = dir.listFiles() ?: return 0
        for (file in files) {
            size += if (file.isDirectory) getFolderSize(file) else file.length()
        }
        return size
    }
}
