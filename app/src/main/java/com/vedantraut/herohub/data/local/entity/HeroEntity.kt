package com.vedantraut.herohub.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "heroes",
    indices = [
        Index(value = ["powerRating"]),
        Index(value = ["publisher"]),
        Index(value = ["alignment"])
    ]
)
data class HeroEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val realName: String = "",
    val imageUrl: String = "",
    val publisher: String = "Unknown",
    val alignment: String = "neutral",
    val powerRating: Int = 50,
    val intelligence: Int = 0,
    val strength: Int = 0,
    val speed: Int = 0,
    val durability: Int = 0,
    val power: Int = 0,
    val combat: Int = 0,
    val gender: String = "",
    val race: String = "",
    val height: String = "",
    val weight: String = "",
    val firstAppearance: String = "",
    val placeOfBirth: String = "",
    val occupation: String = "",
    val groupAffiliation: String = ""
)
