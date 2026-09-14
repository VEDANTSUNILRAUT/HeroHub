package com.vedantraut.herohub.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.usecase.GetAllHeroesUseCase
import com.vedantraut.herohub.domain.usecase.GetHomeHeroesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.vedantraut.herohub.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel(
    private val getHomeHeroesUseCase: GetHomeHeroesUseCase,
    private val getAllHeroesUseCase: GetAllHeroesUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriesState())
    val state: StateFlow<CategoriesState> = _state.asStateFlow()

    private var filterJob: Job? = null

    init {
        loadData()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavoriteHeroIds().collect { ids ->
                _state.update { it.copy(favoriteHeroIds = ids) }
            }
        }
    }

    fun onIntent(intent: CategoriesIntent) {
        when (intent) {
            is CategoriesIntent.LoadCategories -> loadData()
            is CategoriesIntent.Retry -> loadData()
            is CategoriesIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    favoritesRepository.toggleFavorite(intent.heroId)
                }
            }
            is CategoriesIntent.SelectGroup -> {
                _state.update { it.copy(selectedGroup = intent.group) }
            }
            is CategoriesIntent.SelectCategory -> handleCategorySelection(intent.category)
            is CategoriesIntent.SelectSubcategory -> handleSubcategorySelection(intent.subcategory)
            is CategoriesIntent.SetViewMode -> {
                _state.update { it.copy(viewMode = intent.viewMode) }
            }
            is CategoriesIntent.SetSortOrder -> {
                _state.update { it.copy(sortOrder = intent.sortOrder) }
                recalculateCategoryHeroes()
            }
            is CategoriesIntent.SearchWithinCategory -> {
                _state.update { current ->
                    current.copy(searchFilterText = intent.query)
                }
                recalculateCategoryHeroes()
            }
            is CategoriesIntent.SelectHero -> {
                _state.update { current ->
                    val updatedRecent = (listOf(intent.hero) + current.recentlyViewedHeroes)
                        .distinctBy { it.id }
                        .take(8)
                    current.copy(
                        selectedHeroForDetail = intent.hero,
                        recentlyViewedHeroes = updatedRecent
                    )
                }
            }
            is CategoriesIntent.DismissHeroDetail -> {
                _state.update { it.copy(selectedHeroForDetail = null) }
            }
            is CategoriesIntent.LoadMoreHeroes -> handleLoadMoreHeroes()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val heroes = getAllHeroesUseCase()
                val topHeroes = heroes.take(6)
                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        allHeroes = heroes,
                        recentlyViewedHeroes = if (current.recentlyViewedHeroes.isEmpty()) topHeroes else current.recentlyViewedHeroes,
                        error = null
                    )
                }
                recalculateCategoryHeroes()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to load categories"
                    )
                }
            }
        }
    }

    private fun handleCategorySelection(category: CategoryItem?) {
        _state.update { current ->
            current.copy(
                selectedCategory = category,
                selectedSubcategory = null,
                searchFilterText = ""
            )
        }
        recalculateCategoryHeroes()
    }

    private fun handleSubcategorySelection(subcategory: SubcategoryItem?) {
        _state.update { current ->
            val newSubcategory = if (current.selectedSubcategory == subcategory) null else subcategory
            current.copy(selectedSubcategory = newSubcategory)
        }
        recalculateCategoryHeroes()
    }

    private fun recalculateCategoryHeroes() {
        val current = _state.value
        val category = current.selectedCategory ?: return
        val allHeroes = current.allHeroes
        val subcategory = current.selectedSubcategory
        val query = current.searchFilterText
        val sortOrder = current.sortOrder

        filterJob?.cancel()
        filterJob = viewModelScope.launch(Dispatchers.Default) {
            var filtered = filterHeroesByCategory(allHeroes, category)

            subcategory?.let { sub ->
                filtered = filterHeroesBySubcategory(filtered, sub)
            }

            if (query.isNotBlank()) {
                val q = query.trim()
                filtered = filtered.filter {
                    it.name.contains(q, ignoreCase = true) ||
                            it.realName.contains(q, ignoreCase = true) ||
                            it.publisher.contains(q, ignoreCase = true)
                }
            }

            val sorted = sortHeroes(filtered, sortOrder)
            val initialBatch = sorted.take(PAGE_SIZE)
            withContext(Dispatchers.Main) {
                _state.update {
                    it.copy(
                        fullCategoryHeroes = sorted,
                        categoryHeroes = initialBatch,
                        totalCategoryHeroesCount = sorted.size,
                        hasMoreHeroes = sorted.size > initialBatch.size,
                        isLoadingMore = false
                    )
                }
            }
        }
    }

    private fun handleLoadMoreHeroes() {
        val current = _state.value
        if (current.isLoadingMore || !current.hasMoreHeroes) return

        _state.update { it.copy(isLoadingMore = true) }
        viewModelScope.launch(Dispatchers.Default) {
            val nextCount = current.categoryHeroes.size + PAGE_SIZE
            val nextBatch = current.fullCategoryHeroes.take(nextCount)
            withContext(Dispatchers.Main) {
                _state.update {
                    it.copy(
                        categoryHeroes = nextBatch,
                        hasMoreHeroes = current.fullCategoryHeroes.size > nextBatch.size,
                        isLoadingMore = false
                    )
                }
            }
        }
    }

    private fun filterHeroesByCategory(heroes: List<Hero>, category: CategoryItem): List<Hero> {
        return when (category.id) {
            "all_heroes_global" -> heroes
            "marvel" -> heroes.filter { it.publisher.contains("Marvel", ignoreCase = true) }
            "dc" -> heroes.filter { it.publisher.contains("DC", ignoreCase = true) }
            "indie" -> heroes.filter {
                !it.publisher.contains("Marvel", ignoreCase = true) &&
                        !it.publisher.contains("DC", ignoreCase = true) ||
                        it.alignment.equals("neutral", ignoreCase = true)
            }
            "heroes" -> heroes.filter { it.isGood }
            "villains" -> heroes.filter { it.isBad }
            "anti_heroes" -> heroes.filter { !it.isGood && !it.isBad || it.alignment.equals("neutral", ignoreCase = true) }
            "god_tier" -> heroes.filter { it.powerRating >= 90 }
            "heavyweights" -> heroes.filter { it.powerRating in 80..89 }
            "street_tech" -> heroes.filter { it.powerRating < 80 || it.intelligence >= 90 || it.combat >= 90 }
            "mutants" -> heroes.filter {
                it.race.contains("Mutant", ignoreCase = true) ||
                        it.groupAffiliation.contains("X-Men", ignoreCase = true) ||
                        it.name.contains("Wolverine", ignoreCase = true) ||
                        it.name.contains("Deadpool", ignoreCase = true)
            }
            "aliens_gods" -> heroes.filter {
                it.race.contains("Kryptonian", ignoreCase = true) ||
                        it.race.contains("Asgardian", ignoreCase = true) ||
                        it.race.contains("Eternal", ignoreCase = true) ||
                        it.race.contains("Alien", ignoreCase = true) ||
                        it.race.contains("Amazon", ignoreCase = true) ||
                        it.placeOfBirth.contains("Asgard", ignoreCase = true) ||
                        it.placeOfBirth.contains("Krypton", ignoreCase = true) ||
                        it.placeOfBirth.contains("Titan", ignoreCase = true)
            }
            "human_peak" -> heroes.filter {
                it.race.contains("Human", ignoreCase = true) ||
                        it.race.isBlank()
            }
            else -> heroes
        }
    }

    private fun filterHeroesBySubcategory(heroes: List<Hero>, subcategory: SubcategoryItem): List<Hero> {
        return when (subcategory.id) {
            "all_all" -> heroes
            "all_marvel" -> heroes.filter { it.publisher.contains("Marvel", ignoreCase = true) }
            "all_dc" -> heroes.filter { it.publisher.contains("DC", ignoreCase = true) }
            "all_indie" -> heroes.filter { !it.publisher.contains("Marvel", ignoreCase = true) && !it.publisher.contains("DC", ignoreCase = true) }
            "all_heroes" -> heroes.filter { it.isGood }
            "all_villains" -> heroes.filter { it.isBad }
            "avengers" -> heroes.filter { it.groupAffiliation.contains("Avengers", ignoreCase = true) }
            "xmen" -> heroes.filter { it.groupAffiliation.contains("X-Men", ignoreCase = true) || it.race.contains("Mutant", ignoreCase = true) }
            "illuminati" -> heroes.filter { it.groupAffiliation.contains("Illuminati", ignoreCase = true) || it.name.contains("Iron Man", ignoreCase = true) || it.name.contains("Doctor Strange", ignoreCase = true) }
            "cosmic_marvel" -> heroes.filter { it.powerRating >= 90 || it.race.contains("Asgardian", ignoreCase = true) || it.race.contains("Eternal", ignoreCase = true) }
            "justice_league" -> heroes.filter { it.groupAffiliation.contains("Justice League", ignoreCase = true) }
            "bat_family" -> heroes.filter { it.name.contains("Batman", ignoreCase = true) || it.groupAffiliation.contains("Batman", ignoreCase = true) }
            "flash_family" -> heroes.filter { it.name.contains("Flash", ignoreCase = true) || it.groupAffiliation.contains("Flash", ignoreCase = true) }
            "amazons" -> heroes.filter { it.race.contains("Amazon", ignoreCase = true) || it.name.contains("Wonder Woman", ignoreCase = true) }
            "power_95_plus" -> heroes.filter { it.powerRating >= 95 }
            "power_90_94" -> heroes.filter { it.powerRating in 90..94 }
            "power_85_89" -> heroes.filter { it.powerRating in 85..89 }
            "power_80_84" -> heroes.filter { it.powerRating in 80..84 }
            "tech_geniuses" -> heroes.filter { it.intelligence >= 95 || it.name.contains("Iron Man", ignoreCase = true) || it.name.contains("Batman", ignoreCase = true) }
            "street_martial" -> heroes.filter { it.combat >= 85 }
            "super_soldiers" -> heroes.filter { it.name.contains("Captain America", ignoreCase = true) || it.strength >= 70 }
            "detectives" -> heroes.filter { it.name.contains("Batman", ignoreCase = true) || it.intelligence >= 95 }
            else -> heroes
        }
    }

    private fun sortHeroes(heroes: List<Hero>, sortOrder: CategorySortOrder): List<Hero> {
        return when (sortOrder) {
            CategorySortOrder.POWER_DESC -> heroes.sortedByDescending { it.powerRating }
            CategorySortOrder.POWER_ASC -> heroes.sortedBy { it.powerRating }
            CategorySortOrder.NAME_ASC -> heroes.sortedBy { it.name.lowercase() }
            CategorySortOrder.NAME_DESC -> heroes.sortedByDescending { it.name.lowercase() }
        }
    }

    companion object {
        const val PAGE_SIZE = 36
    }
}
