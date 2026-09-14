package com.vedantraut.herohub.domain.usecase

import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.repository.HeroRepository

class GetAllHeroesUseCase(
    private val heroRepository: HeroRepository
) {
    suspend operator fun invoke(): List<Hero> {
        return heroRepository.getAllHeroes()
    }
}
