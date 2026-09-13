package com.vedantraut.herohub.presentation.search

import com.vedantraut.herohub.domain.model.Hero

sealed interface SearchIntent {
    data class UpdateQuery(val query: String) : SearchIntent
    data class ExecuteSearch(val query: String) : SearchIntent
    data object ClearQuery : SearchIntent
    data class SelectSuggestion(val suggestion: String) : SearchIntent
    data class DeleteRecentSearch(val query: String) : SearchIntent
    data object ClearAllRecentSearches : SearchIntent
    data object OpenFilterSheet : SearchIntent
    data object CloseFilterSheet : SearchIntent
    data class ApplyFilters(val filters: SearchFilterState) : SearchIntent
    data object ResetFilters : SearchIntent
    data class ToggleVoiceSearchDialog(val isOpen: Boolean) : SearchIntent
    data class SubmitVoiceResult(val text: String) : SearchIntent
    data class SetViewMode(val viewMode: SearchViewMode) : SearchIntent
    data class SelectHero(val hero: Hero) : SearchIntent
    data class ToggleFavorite(val heroId: String) : SearchIntent
    data object DismissHeroDetail : SearchIntent
    data object Retry : SearchIntent
}
