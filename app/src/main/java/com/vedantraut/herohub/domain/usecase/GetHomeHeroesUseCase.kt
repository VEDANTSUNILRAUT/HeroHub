package com.vedantraut.herohub.domain.usecase

import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.repository.HeroRepository

class GetHomeHeroesUseCase(
    private val repository: HeroRepository
) {
    suspend operator fun invoke(): List<Hero> {
        return repository.getHomeHeroes()
    }
}
