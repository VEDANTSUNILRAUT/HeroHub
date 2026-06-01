package com.vedantraut.herohub.domain.usecase

import com.vedantraut.herohub.domain.repository.HeroRepository

class SearchHeroesUseCase(
    private val repository: HeroRepository
) {

    suspend operator fun invoke(name: String) =
        repository.searchHeroes(name)
}