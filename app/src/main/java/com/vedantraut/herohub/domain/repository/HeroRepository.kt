package com.vedantraut.herohub.domain.repository

import com.vedantraut.herohub.domain.model.Hero

interface HeroRepository {
    suspend fun searchHeroes(name: String): List<Hero>
    suspend fun getHomeHeroes(): List<Hero>
}