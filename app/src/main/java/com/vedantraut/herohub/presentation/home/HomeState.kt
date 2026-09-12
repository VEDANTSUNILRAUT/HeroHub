package com.vedantraut.herohub.presentation.home

import com.vedantraut.herohub.domain.model.Hero

data class HomeState(
    val isLoading: Boolean = true,
    val featuredHeroes: List<Hero> = emptyList(),
    val popularHeroes: List<Hero> = emptyList(),
    val powerRankedHeroes: List<Hero> = emptyList(),
    val recentlyAddedHeroes: List<Hero> = emptyList(),
    val allHeroes: List<Hero> = emptyList(),
    val filteredHeroes: List<Hero> = emptyList(),
    val categories: List<String> = listOf("All", "Marvel", "DC Comics", "Heroes", "Villains"),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val selectedHeroForDetail: Hero? = null,
    val error: String? = null
)