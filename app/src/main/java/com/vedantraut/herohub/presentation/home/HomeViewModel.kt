package com.vedantraut.herohub.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herohub.presentation.home.HomeIntent
import com.herohub.presentation.home.HomeState
import com.vedantraut.herohub.domain.usecase.SearchHeroesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val searchHeroesUseCase: SearchHeroesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun onIntent(intent: HomeIntent) {
        when (intent) {

            is HomeIntent.SearchHero -> {
                searchHeroes(intent.query)
            }
        }
    }

    private fun searchHeroes(query: String) {

        if (query.isBlank()) {
            _state.value = HomeState()
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            try {

                val heroes = searchHeroesUseCase(query)

                _state.value = HomeState(
                    heroes = heroes,
                    isLoading = false
                )

            } catch (e: Exception) {

                _state.value = HomeState(
                    error = e.message ?: "Unknown Error",
                    isLoading = false
                )
            }
        }
    }
}