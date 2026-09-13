package com.vedantraut.herohub.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.repository.FavoritesRepository
import com.vedantraut.herohub.domain.usecase.GetHomeHeroesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val getHomeHeroesUseCase: GetHomeHeroesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    private var allLoadedHeroes: List<Hero> = emptyList()

    init {
        observeFavoritesAndLoadHeroes()
    }

    fun onIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadFavorites -> observeFavoritesAndLoadHeroes()
            is FavoritesIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    favoritesRepository.toggleFavorite(intent.heroId)
                }
            }
            is FavoritesIntent.AddFavorite -> {
                viewModelScope.launch {
                    favoritesRepository.addFavorite(intent.heroId)
                }
            }
            is FavoritesIntent.RemoveFavorite -> {
                viewModelScope.launch {
                    favoritesRepository.removeFavorite(intent.heroId)
                }
            }
            is FavoritesIntent.SearchFavorites -> {
                _state.update { it.copy(searchQuery = intent.query) }
                applyFilterAndSort()
            }
            is FavoritesIntent.SelectPublisher -> {
                _state.update { it.copy(selectedPublisher = intent.publisher) }
                applyFilterAndSort()
            }
            is FavoritesIntent.SelectAlignment -> {
                _state.update { it.copy(selectedAlignment = intent.alignment) }
                applyFilterAndSort()
            }
            is FavoritesIntent.SetSortOrder -> {
                _state.update { it.copy(sortOrder = intent.sortOrder) }
                applyFilterAndSort()
            }
            is FavoritesIntent.SetViewMode -> {
                _state.update { it.copy(viewMode = intent.viewMode) }
            }
            is FavoritesIntent.SelectHero -> {
                _state.update { it.copy(selectedHeroForDetail = intent.hero) }
            }
            is FavoritesIntent.DismissHeroDetail -> {
                _state.update { it.copy(selectedHeroForDetail = null) }
            }
            is FavoritesIntent.ShowClearDialog -> {
                _state.update { it.copy(showClearConfirmDialog = intent.show) }
            }
            is FavoritesIntent.ConfirmClearAllFavorites -> {
                viewModelScope.launch {
                    favoritesRepository.clearFavorites()
                    _state.update { it.copy(showClearConfirmDialog = false) }
                }
            }
        }
    }

    private fun observeFavoritesAndLoadHeroes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                allLoadedHeroes = getHomeHeroesUseCase()
            } catch (_: Exception) {}

            favoritesRepository.getFavoriteHeroIds().collect { ids ->
                val favs = allLoadedHeroes.filter { ids.contains(it.id) }
                val recommended = allLoadedHeroes.filterNot { ids.contains(it.id) }.take(6)
                val avgPower = if (favs.isNotEmpty()) favs.map { it.powerRating }.average().toInt() else 0
                val top = favs.maxByOrNull { it.powerRating }

                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        favoriteHeroIds = ids,
                        favoriteHeroes = favs,
                        recommendedHeroes = recommended,
                        averagePower = avgPower,
                        topHero = top
                    )
                }
                applyFilterAndSort()
            }
        }
    }

    private fun applyFilterAndSort() {
        val current = _state.value
        val query = current.searchQuery.trim()

        var filtered = current.favoriteHeroes.filter { hero ->
            val matchesQuery = query.isBlank() ||
                    hero.name.contains(query, ignoreCase = true) ||
                    hero.realName.contains(query, ignoreCase = true) ||
                    hero.publisher.contains(query, ignoreCase = true)

            val matchesPublisher = when (current.selectedPublisher) {
                "Marvel" -> hero.publisher.contains("Marvel", ignoreCase = true)
                "DC" -> hero.publisher.contains("DC", ignoreCase = true)
                "Indie" -> !hero.publisher.contains("Marvel", ignoreCase = true) && !hero.publisher.contains("DC", ignoreCase = true)
                else -> true
            }

            val matchesAlignment = when (current.selectedAlignment) {
                "Hero" -> hero.isGood
                "Villain" -> hero.isBad
                "Anti-Hero" -> !hero.isGood && !hero.isBad || hero.alignment.equals("neutral", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesPublisher && matchesAlignment
        }

        filtered = when (current.sortOrder) {
            FavoritesSortOrder.POWER_DESC -> filtered.sortedByDescending { it.powerRating }
            FavoritesSortOrder.POWER_ASC -> filtered.sortedBy { it.powerRating }
            FavoritesSortOrder.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
            FavoritesSortOrder.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
        }

        _state.update { it.copy(filteredHeroes = filtered) }
    }
}
