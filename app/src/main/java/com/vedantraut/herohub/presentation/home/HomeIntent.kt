package com.herohub.presentation.home

sealed class HomeIntent {
    data class SearchHero(val query: String) : HomeIntent()
}