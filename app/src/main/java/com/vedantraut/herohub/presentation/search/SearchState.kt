package com.vedantraut.herohub.presentation.search

import androidx.compose.runtime.Immutable
import com.vedantraut.herohub.domain.model.Hero

@Immutable
data class SearchState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val allHeroes: List<Hero> = emptyList(),
    val searchResults: List<Hero> = emptyList(),
    val autocompleteSuggestions: List<String> = emptyList(),
    val recentSearches: List<String> = listOf("Spider-Man", "Batman", "Iron Man", "Thanos", "Wolverine"),
    val trendingSearches: List<String> = listOf("Spider-Man", "Batman", "Iron Man", "Thanos", "Deadpool", "Thor", "Wolverine", "Flash", "Hulk", "Wonder Woman"),
    val filters: SearchFilterState = SearchFilterState(),
    val isFilterSheetOpen: Boolean = false,
    val isVoiceSearchDialogShown: Boolean = false,
    val viewMode: SearchViewMode = SearchViewMode.GRID,
    val selectedHeroForDetail: Hero? = null,
    val favoriteHeroIds: Set<String> = emptySet(),
    val isOffline: Boolean = false,
    val error: String? = null
) {
    val isQueryActive: Boolean
        get() = searchQuery.isNotBlank() || !filters.isDefault
}
