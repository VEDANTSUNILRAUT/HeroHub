package com.herohub.presentation.home

import com.vedantraut.herohub.domain.model.Hero

data class HomeState(
    val isLoading: Boolean = false,
    val heroes: List<Hero> = emptyList(),
    val error: String? = null
)