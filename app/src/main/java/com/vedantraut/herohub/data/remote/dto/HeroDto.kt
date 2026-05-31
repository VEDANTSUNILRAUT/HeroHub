package com.vedantraut.herohub.data.remote.dto

data class HeroDto(
    val id: String,
    val name: String,
    val image: ImageDto
)

data class ImageDto(
    val url: String
)