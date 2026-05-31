package com.vedantraut.herohub.data.remote.api

import com.vedantraut.herohub.data.remote.dto.HeroSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface HeroApi {

    @GET("{token}/search/{name}")
    suspend fun searchHeroes(
        @Path("token") token: String,
        @Path("name") name: String
    ): HeroSearchResponseDto
}