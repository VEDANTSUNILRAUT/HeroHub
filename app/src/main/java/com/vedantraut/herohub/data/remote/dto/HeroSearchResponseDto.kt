package com.vedantraut.herohub.data.remote.dto

data class HeroSearchResponseDto(
    val response: String,
    val results: List<HeroDto>?,
    val error: String?
)