package com.vedantraut.herohub.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoriteHeroIds(): Flow<Set<String>>
    suspend fun toggleFavorite(heroId: String)
    suspend fun addFavorite(heroId: String)
    suspend fun removeFavorite(heroId: String)
    suspend fun isFavorite(heroId: String): Boolean
    suspend fun clearFavorites()
}
