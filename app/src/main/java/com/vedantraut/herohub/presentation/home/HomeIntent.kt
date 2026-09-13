package com.vedantraut.herohub.presentation.home

import com.vedantraut.herohub.domain.model.Hero

sealed interface HomeIntent {
    data object LoadHomeData : HomeIntent
    data class SearchHero(val query: String) : HomeIntent
    data class SelectCategory(val category: String) : HomeIntent
    data class SelectHero(val hero: Hero) : HomeIntent
    data class ToggleFavorite(val heroId: String) : HomeIntent
    data object DismissHeroDetail : HomeIntent
    data object Retry : HomeIntent
}