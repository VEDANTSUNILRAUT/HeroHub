package com.vedantraut.herohub.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.usecase.GetHomeHeroesUseCase
import com.vedantraut.herohub.domain.usecase.SearchHeroesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.vedantraut.herohub.domain.repository.FavoritesRepository

class HomeViewModel(
    private val getHomeHeroesUseCase: GetHomeHeroesUseCase,
    private val searchHeroesUseCase: SearchHeroesUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadHomeData()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavoriteHeroIds().collect { ids ->
                _state.update { it.copy(favoriteHeroIds = ids) }
            }
        }
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadHomeData -> loadHomeData()
            is HomeIntent.Retry -> loadHomeData()
            is HomeIntent.SearchHero -> handleSearch(intent.query)
            is HomeIntent.SelectCategory -> handleCategorySelection(intent.category)
            is HomeIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    favoritesRepository.toggleFavorite(intent.heroId)
                }
            }
            is HomeIntent.SelectHero -> {
                _state.update { it.copy(selectedHeroForDetail = intent.hero) }
            }
            is HomeIntent.DismissHeroDetail -> {
                _state.update { it.copy(selectedHeroForDetail = null) }
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val heroes = getHomeHeroesUseCase()
                val featured = heroes.take(5)
                val popular = heroes.filter { it.powerRating >= 85 }.take(6).ifEmpty { heroes.take(6) }
                val powerRanked = heroes.sortedByDescending { it.powerRating }.take(5)
                val recentlyAdded = heroes.reversed().take(6)
                val filtered = applyFilter(heroes, _state.value.searchQuery, _state.value.selectedCategory)

                _state.update {
                    it.copy(
                        isLoading = false,
                        allHeroes = heroes,
                        featuredHeroes = featured,
                        popularHeroes = popular,
                        powerRankedHeroes = powerRanked,
                        recentlyAddedHeroes = recentlyAdded,
                        filteredHeroes = filtered,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to load superheroes"
                    )
                }
            }
        }
    }

    private fun handleCategorySelection(category: String) {
        _state.update { current ->
            val filtered = applyFilter(current.allHeroes, current.searchQuery, category)
            current.copy(
                selectedCategory = category,
                filteredHeroes = filtered
            )
        }
    }

    private fun handleSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val localFiltered = applyFilter(_state.value.allHeroes, query, _state.value.selectedCategory)
            _state.update { it.copy(filteredHeroes = localFiltered) }

            if (query.isNotBlank() && query.length >= 2) {
                delay(400)
                try {
                    val remoteResults = searchHeroesUseCase(query)
                    if (remoteResults.isNotEmpty()) {
                        val combined = (_state.value.allHeroes + remoteResults).distinctBy { it.id }
                        val filtered = applyFilter(combined, query, _state.value.selectedCategory)
                        _state.update {
                            it.copy(
                                allHeroes = combined,
                                filteredHeroes = filtered
                            )
                        }
                    }
                } catch (_: Exception) {
                    // Fallback to existing local search results
                }
            }
        }
    }

    private fun applyFilter(heroes: List<Hero>, query: String, category: String): List<Hero> {
        return heroes.filter { hero ->
            val matchesQuery = query.isBlank() ||
                    hero.name.contains(query, ignoreCase = true) ||
                    hero.realName.contains(query, ignoreCase = true) ||
                    hero.publisher.contains(query, ignoreCase = true)

            val matchesCategory = when (category) {
                "Marvel" -> hero.publisher.contains("Marvel", ignoreCase = true)
                "DC Comics" -> hero.publisher.contains("DC", ignoreCase = true)
                "Heroes" -> hero.isGood
                "Villains" -> hero.isBad
                else -> true
            }

            matchesQuery && matchesCategory
        }
    }
}