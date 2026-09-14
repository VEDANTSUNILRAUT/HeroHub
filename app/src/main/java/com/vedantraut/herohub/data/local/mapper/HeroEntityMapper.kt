package com.vedantraut.herohub.data.local.mapper

import com.vedantraut.herohub.data.local.entity.HeroEntity
import com.vedantraut.herohub.domain.model.Hero

fun HeroEntity.toDomain(): Hero {
    return Hero(
        id = id,
        name = name,
        realName = realName,
        imageUrl = imageUrl,
        publisher = publisher,
        alignment = alignment,
        powerRating = powerRating,
        intelligence = intelligence,
        strength = strength,
        speed = speed,
        durability = durability,
        power = power,
        combat = combat,
        gender = gender,
        race = race,
        height = height,
        weight = weight,
        firstAppearance = firstAppearance,
        placeOfBirth = placeOfBirth,
        occupation = occupation,
        groupAffiliation = groupAffiliation
    )
}

fun Hero.toEntity(): HeroEntity {
    return HeroEntity(
        id = id,
        name = name,
        realName = realName,
        imageUrl = imageUrl,
        publisher = publisher,
        alignment = alignment,
        powerRating = powerRating,
        intelligence = intelligence,
        strength = strength,
        speed = speed,
        durability = durability,
        power = power,
        combat = combat,
        gender = gender,
        race = race,
        height = height,
        weight = weight,
        firstAppearance = firstAppearance,
        placeOfBirth = placeOfBirth,
        occupation = occupation,
        groupAffiliation = groupAffiliation
    )
}
