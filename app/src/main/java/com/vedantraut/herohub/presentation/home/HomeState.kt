package com.vedantraut.herohub.presentation.home

import androidx.compose.runtime.Immutable
import com.vedantraut.herohub.domain.model.Hero

@Immutable
data class HomeState(
    val isLoading: Boolean = true,
    val featuredHeroes: List<Hero> = emptyList(),
    val popularHeroes: List<Hero> = emptyList(),
    val powerRankedHeroes: List<Hero> = emptyList(),
    val recentlyAddedHeroes: List<Hero> = emptyList(),
    val allHeroes: List<Hero> = emptyList(),
    val filteredHeroes: List<Hero> = emptyList(),
    val clashHeroes: Pair<Hero, Hero>? = null,
    val categories: List<String> = listOf("All", "Marvel", "DC Comics", "Indie", "Cosmic", "Heroes", "Villains"),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val favoriteHeroIds: Set<String> = emptySet(),
    val selectedHeroForDetail: Hero? = null,
    val error: String? = null
)