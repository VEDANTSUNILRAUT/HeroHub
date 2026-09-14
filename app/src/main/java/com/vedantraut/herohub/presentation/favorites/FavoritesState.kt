package com.vedantraut.herohub.presentation.favorites

import androidx.compose.runtime.Immutable
import com.vedantraut.herohub.domain.model.Hero

@Immutable
data class FavoritesState(
    val isLoading: Boolean = true,
    val favoriteHeroIds: Set<String> = emptySet(),
    val favoriteHeroes: List<Hero> = emptyList(),
    val filteredHeroes: List<Hero> = emptyList(),
    val recommendedHeroes: List<Hero> = emptyList(),
    val searchQuery: String = "",
    val selectedPublisher: String = "All",
    val selectedAlignment: String = "All",
    val sortOrder: FavoritesSortOrder = FavoritesSortOrder.POWER_DESC,
    val viewMode: FavoritesViewMode = FavoritesViewMode.GRID,
    val selectedHeroForDetail: Hero? = null,
    val averagePower: Int = 0,
    val topHero: Hero? = null,
    val showClearConfirmDialog: Boolean = false
) {
    val isEmptySquad: Boolean get() = favoriteHeroes.isEmpty()
}
