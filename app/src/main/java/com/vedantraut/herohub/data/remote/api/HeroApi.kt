package com.vedantraut.herohub.data.remote.api

import com.vedantraut.herohub.data.remote.dto.AkababHeroDto
import com.vedantraut.herohub.data.remote.dto.HeroDto
import com.vedantraut.herohub.data.remote.dto.HeroSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface HeroApi {

    @GET("{token}/search/{name}")
    suspend fun searchHeroes(
        @Path("token") token: String,
        @Path("name") name: String
    ): HeroSearchResponseDto

    @GET("{token}/{id}")
    suspend fun getHeroById(
        @Path("token") token: String,
        @Path("id") id: String
    ): HeroDto

    @GET("https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/all.json")
    suspend fun getAllHeroesFromApi(): List<AkababHeroDto>
}