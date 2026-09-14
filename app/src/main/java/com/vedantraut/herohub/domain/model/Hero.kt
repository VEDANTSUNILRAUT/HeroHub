package com.vedantraut.herohub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Hero(
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
) {
    val isGood: Boolean
        get() = alignment.equals("good", ignoreCase = true)

    val isBad: Boolean
        get() = alignment.equals("bad", ignoreCase = true)

    val displayAlignment: String
        get() = when (alignment.lowercase()) {
            "good" -> "Hero"
            "bad" -> "Villain"
            else -> "Anti-Hero"
        }
}