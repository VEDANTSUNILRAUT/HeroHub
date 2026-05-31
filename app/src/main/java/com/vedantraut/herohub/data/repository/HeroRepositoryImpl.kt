package com.vedantraut.herohub.data.repository

import android.util.Log
import com.vedantraut.herohub.core.network.NetworkConstants.ACCESS_TOKEN
import com.vedantraut.herohub.data.remote.api.HeroApi
import com.vedantraut.herohub.data.remote.mapper.toDomain
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.repository.HeroRepository

class HeroRepositoryImpl(
    private val api: HeroApi
) : HeroRepository {

    override suspend fun searchHeroes(name: String): List<Hero> {
        val response = api.searchHeroes(
            token = ACCESS_TOKEN,
            name = name
        )
        return response.results?.map {
            it.toDomain()
        } ?: emptyList()
    }
}
