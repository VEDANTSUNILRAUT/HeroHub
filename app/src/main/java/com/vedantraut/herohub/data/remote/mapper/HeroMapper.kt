package com.vedantraut.herohub.data.remote.mapper

import com.vedantraut.herohub.data.remote.dto.HeroDto
import com.vedantraut.herohub.domain.model.Hero


fun HeroDto.toDomain(): Hero {
    return Hero(
        id = id,
        name = name,
        imageUrl = image.url
    )
}