package com.vedantraut.herohub.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.vedantraut.herohub.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesRepositoryImpl(
    context: Context
) : FavoritesRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("herohub_favorites", Context.MODE_PRIVATE)
    private val keyFavorites = "favorite_hero_ids"

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        val saved = prefs.getStringSet(keyFavorites, null)
        val initialSet = saved ?: setOf("70", "620") // Default Batman & Spider-Man on initial install
        if (saved == null) {
            prefs.edit().putStringSet(keyFavorites, initialSet).apply()
        }
        _favoriteIds.value = initialSet
    }

    override fun getFavoriteHeroIds(): Flow<Set<String>> = _favoriteIds.asStateFlow()

    override suspend fun toggleFavorite(heroId: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(heroId)) {
            current.remove(heroId)
        } else {
            current.add(heroId)
        }
        saveSet(current)
    }

    override suspend fun addFavorite(heroId: String) {
        val current = _favoriteIds.value.toMutableSet()
        current.add(heroId)
        saveSet(current)
    }

    override suspend fun removeFavorite(heroId: String) {
        val current = _favoriteIds.value.toMutableSet()
        current.remove(heroId)
        saveSet(current)
    }

    override suspend fun isFavorite(heroId: String): Boolean {
        return _favoriteIds.value.contains(heroId)
    }

    override suspend fun clearFavorites() {
        saveSet(emptySet())
    }

    private fun saveSet(newSet: Set<String>) {
        _favoriteIds.value = newSet
        prefs.edit().putStringSet(keyFavorites, newSet).apply()
    }
}
