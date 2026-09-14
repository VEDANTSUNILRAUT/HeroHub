package com.vedantraut.herohub.data.repository

import android.util.Log
import com.vedantraut.herohub.core.network.NetworkConstants.ACCESS_TOKEN
import com.vedantraut.herohub.data.local.catalog.CuratedHeroCatalog
import com.vedantraut.herohub.data.local.dao.HeroDao
import com.vedantraut.herohub.data.local.mapper.toDomain as entityToDomain
import com.vedantraut.herohub.data.local.mapper.toEntity as domainToEntity
import com.vedantraut.herohub.data.remote.api.HeroApi
import com.vedantraut.herohub.data.remote.dto.toDomain as akababToDomain
import com.vedantraut.herohub.data.remote.mapper.toDomain as remoteToDomain
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.repository.HeroRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HeroRepositoryImpl(
    private val api: HeroApi,
    private val heroDao: HeroDao
) : HeroRepository {

    override suspend fun searchHeroes(name: String): List<Hero> = withContext(Dispatchers.IO) {
        ensureDatabaseSeeded()

        // 1. Instant local Room search
        val localMatches = heroDao.searchHeroes(name).map { it.entityToDomain() }
        if (localMatches.isNotEmpty()) {
            return@withContext localMatches
        }

        // 2. Remote API query if no local match
        try {
            val response = api.searchHeroes(
                token = ACCESS_TOKEN,
                name = name
            )
            val remoteResults = response.results?.map { it.remoteToDomain() } ?: emptyList()
            if (remoteResults.isNotEmpty()) {
                heroDao.insertHeroes(remoteResults.map { it.domainToEntity() })
                return@withContext remoteResults
            }
        } catch (e: Exception) {
            Log.w("HeroRepository", "Remote search failed: ${e.message}")
        }

        // 3. Fallback catalog search
        CuratedHeroCatalog.getCuratedHeroes().filter {
            it.name.contains(name, ignoreCase = true) ||
                    it.realName.contains(name, ignoreCase = true) ||
                    it.publisher.contains(name, ignoreCase = true)
        }
    }

    override suspend fun getHomeHeroes(): List<Hero> = withContext(Dispatchers.IO) {
        ensureDatabaseSeeded()

        val cachedEntities = heroDao.getAllHeroes()
        if (cachedEntities.isNotEmpty()) {
            return@withContext cachedEntities.map { it.entityToDomain() }
        }

        CuratedHeroCatalog.getCuratedHeroes()
    }

    override suspend fun getAllHeroes(): List<Hero> = withContext(Dispatchers.IO) {
        ensureDatabaseSeeded()

        val localCount = heroDao.getHeroCount()
        if (localCount >= 500) {
            return@withContext heroDao.getAllHeroes().map { it.entityToDomain() }
        }

        // Extract complete 700+ registry from API
        try {
            val apiHeroes = api.getAllHeroesFromApi()
            if (apiHeroes.isNotEmpty()) {
                val domainHeroes = apiHeroes.map { it.akababToDomain() }
                val entities = domainHeroes.map { it.domainToEntity() }
                entities.chunked(100).forEach { batch ->
                    heroDao.insertHeroes(batch)
                }
                return@withContext domainHeroes
            }
        } catch (e: Exception) {
            Log.e("HeroRepository", "Error extracting all 700+ heroes from API: ${e.message}")
        }

        val cached = heroDao.getAllHeroes()
        if (cached.isNotEmpty()) {
            cached.map { it.entityToDomain() }
        } else {
            CuratedHeroCatalog.getCuratedHeroes()
        }
    }

    private suspend fun ensureDatabaseSeeded() {
        try {
            val count = heroDao.getHeroCount()
            if (count == 0) {
                val curated = CuratedHeroCatalog.getCuratedHeroes()
                heroDao.insertHeroes(curated.map { it.domainToEntity() })
            }
        } catch (e: Exception) {
            Log.e("HeroRepository", "Error seeding Room database: ${e.message}")
        }
    }
}
