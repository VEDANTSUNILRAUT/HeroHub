package com.vedantraut.herohub.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.usecase.GetHomeHeroesUseCase
import com.vedantraut.herohub.domain.usecase.SearchHeroesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.vedantraut.herohub.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHeroesUseCase: SearchHeroesUseCase,
    private val getHomeHeroesUseCase: GetHomeHeroesUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadInitialData()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavoriteHeroIds().collect { ids ->
                _state.update { it.copy(favoriteHeroIds = ids) }
            }
        }
    }

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateQuery -> handleQueryUpdate(intent.query)
            is SearchIntent.ExecuteSearch -> executeSearch(intent.query)
            is SearchIntent.ClearQuery -> handleClearQuery()
            is SearchIntent.SelectSuggestion -> {
                executeSearch(intent.suggestion)
            }
            is SearchIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    favoritesRepository.toggleFavorite(intent.heroId)
                }
            }
            is SearchIntent.DeleteRecentSearch -> {
                _state.update { current ->
                    current.copy(recentSearches = current.recentSearches.filterNot { it.equals(intent.query, ignoreCase = true) })
                }
            }
            is SearchIntent.ClearAllRecentSearches -> {
                _state.update { it.copy(recentSearches = emptyList()) }
            }
            is SearchIntent.OpenFilterSheet -> {
                _state.update { it.copy(isFilterSheetOpen = true) }
            }
            is SearchIntent.CloseFilterSheet -> {
                _state.update { it.copy(isFilterSheetOpen = false) }
            }
            is SearchIntent.ApplyFilters -> {
                _state.update { it.copy(filters = intent.filters, isFilterSheetOpen = false) }
                applyCurrentFiltersAndSort()
            }
            is SearchIntent.ResetFilters -> {
                _state.update { it.copy(filters = SearchFilterState(), isFilterSheetOpen = false) }
                applyCurrentFiltersAndSort()
            }
            is SearchIntent.ToggleVoiceSearchDialog -> {
                _state.update { it.copy(isVoiceSearchDialogShown = intent.isOpen) }
            }
            is SearchIntent.SubmitVoiceResult -> {
                _state.update { it.copy(isVoiceSearchDialogShown = false) }
                executeSearch(intent.text)
            }
            is SearchIntent.SetViewMode -> {
                _state.update { it.copy(viewMode = intent.viewMode) }
            }
            is SearchIntent.SelectHero -> {
                _state.update { it.copy(selectedHeroForDetail = intent.hero) }
            }
            is SearchIntent.DismissHeroDetail -> {
                _state.update { it.copy(selectedHeroForDetail = null) }
            }
            is SearchIntent.Retry -> {
                executeSearch(_state.value.searchQuery)
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                val heroes = getHomeHeroesUseCase()
                _state.update { it.copy(allHeroes = heroes) }
            } catch (e: Exception) {
                // Ignore initial background load error
            }
        }
    }

    private fun handleQueryUpdate(query: String) {
        _state.update { current ->
            val suggestions = if (query.isBlank()) {
                emptyList()
            } else {
                current.allHeroes
                    .filter { it.name.contains(query, ignoreCase = true) || it.realName.contains(query, ignoreCase = true) }
                    .map { it.name }
                    .distinct()
                    .take(6)
            }
            current.copy(
                searchQuery = query,
                autocompleteSuggestions = suggestions
            )
        }

        searchJob?.cancel()
        if (query.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(350)
                performSearch(query, addToHistory = false)
            }
        } else {
            applyCurrentFiltersAndSort()
        }
    }

    private fun handleClearQuery() {
        searchJob?.cancel()
        _state.update {
            it.copy(
                searchQuery = "",
                autocompleteSuggestions = emptyList(),
                searchResults = emptyList(),
                isLoading = false
            )
        }
        applyCurrentFiltersAndSort()
    }

    private fun executeSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return

        searchJob?.cancel()
        _state.update { current ->
            val updatedRecents = (listOf(trimmed) + current.recentSearches)
                .distinctBy { it.lowercase() }
                .take(8)
            current.copy(
                searchQuery = trimmed,
                autocompleteSuggestions = emptyList(),
                recentSearches = updatedRecents
            )
        }

        performSearch(trimmed, addToHistory = true)
    }

    private fun performSearch(query: String, addToHistory: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // 1. Local search from loaded heroes
            val localMatches = _state.value.allHeroes.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.realName.contains(query, ignoreCase = true) ||
                        it.publisher.contains(query, ignoreCase = true)
            }

            try {
                val remoteResults = searchHeroesUseCase(query)
                val combined = (localMatches + remoteResults + _state.value.allHeroes)
                    .distinctBy { it.id }

                _state.update { current ->
                    current.copy(
                        allHeroes = combined,
                        isLoading = false,
                        isOffline = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                // Offline fallback
                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        isOffline = true
                    )
                }
            }

            applyCurrentFiltersAndSort()
        }
    }

    private fun applyCurrentFiltersAndSort() {
        val current = _state.value
        val query = current.searchQuery.trim()

        var filtered = current.allHeroes.filter { hero ->
            val matchesQuery = if (query.isBlank()) {
                !current.filters.isDefault // If no query, only show heroes if non-default filters are active
            } else {
                hero.name.contains(query, ignoreCase = true) ||
                        hero.realName.contains(query, ignoreCase = true) ||
                        hero.publisher.contains(query, ignoreCase = true)
            }

            val matchesPublisher = when (current.filters.publisher) {
                "Marvel" -> hero.publisher.contains("Marvel", ignoreCase = true)
                "DC" -> hero.publisher.contains("DC", ignoreCase = true)
                "Indie" -> !hero.publisher.contains("Marvel", ignoreCase = true) && !hero.publisher.contains("DC", ignoreCase = true)
                else -> true
            }

            val matchesAlignment = when (current.filters.alignment) {
                "Hero" -> hero.isGood
                "Villain" -> hero.isBad
                "Anti-Hero" -> !hero.isGood && !hero.isBad || hero.alignment.equals("neutral", ignoreCase = true)
                else -> true
            }

            val matchesPower = hero.powerRating in current.filters.minPower..current.filters.maxPower

            val matchesGender = when (current.filters.gender) {
                "Male" -> hero.gender.equals("Male", ignoreCase = true)
                "Female" -> hero.gender.equals("Female", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesPublisher && matchesAlignment && matchesPower && matchesGender
        }

        // Apply sort
        filtered = when (current.filters.sortOrder) {
            SearchSortOrder.RELEVANCE -> {
                if (query.isBlank()) {
                    filtered.sortedByDescending { it.powerRating }
                } else {
                    filtered.sortedWith(
                        compareByDescending<Hero> { it.name.startsWith(query, ignoreCase = true) }
                            .thenByDescending { it.name.contains(query, ignoreCase = true) }
                            .thenByDescending { it.powerRating }
                    )
                }
            }
            SearchSortOrder.POWER_DESC -> filtered.sortedByDescending { it.powerRating }
            SearchSortOrder.POWER_ASC -> filtered.sortedBy { it.powerRating }
            SearchSortOrder.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
            SearchSortOrder.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
        }

        _state.update { it.copy(searchResults = filtered) }
    }
}
