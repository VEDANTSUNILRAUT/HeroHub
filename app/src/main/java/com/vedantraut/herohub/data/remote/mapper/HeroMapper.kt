package com.vedantraut.herohub.data.remote.mapper

import com.vedantraut.herohub.data.remote.dto.HeroDto
import com.vedantraut.herohub.domain.model.Hero

fun HeroDto.toDomain(): Hero {
    val intel = powerstats?.intelligence?.toIntOrNull() ?: 0
    val str = powerstats?.strength?.toIntOrNull() ?: 0
    val spd = powerstats?.speed?.toIntOrNull() ?: 0
    val dur = powerstats?.durability?.toIntOrNull() ?: 0
    val pwr = powerstats?.power?.toIntOrNull() ?: 0
    val cmb = powerstats?.combat?.toIntOrNull() ?: 0

    val statsList = listOf(intel, str, spd, dur, pwr, cmb).filter { it > 0 }
    val avgRating = if (statsList.isNotEmpty()) statsList.average().toInt() else 50

    val realNameVal = biography?.fullName?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
        ?: biography?.alterEgos?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) && !it.contains("no alter egos", ignoreCase = true) }
        ?: name

    val pub = biography?.publisher?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
        ?: "Unknown"

    val align = biography?.alignment?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
        ?: "neutral"

    val heightStr = appearance?.height?.filter { it.isNotBlank() && it != "-" && it != "0 cm" }?.joinToString(" / ")
        ?: ""

    val weightStr = appearance?.weight?.filter { it.isNotBlank() && it != "-" && it != "0 kg" }?.joinToString(" / ")
        ?: ""

    val rawUrl = image?.url?.replace("http://", "https://")?.takeIf { it.isNotBlank() && it != "null" } ?: ""
    val cdnUrl = if (id.isNotBlank() && name.isNotBlank()) generateCdnHeroImageUrl(id, name) else ""
    val finalImageUrl = if (cdnUrl.isNotBlank()) cdnUrl else rawUrl

    return Hero(
        id = id,
        name = name,
        realName = realNameVal,
        imageUrl = finalImageUrl,
        publisher = pub,
        alignment = align,
        powerRating = avgRating,
        intelligence = intel,
        strength = str,
        speed = spd,
        durability = dur,
        power = pwr,
        combat = cmb,
        gender = appearance?.gender?.takeIf { it != "-" && !it.equals("null", ignoreCase = true) } ?: "",
        race = appearance?.race?.takeIf { it != "-" && !it.equals("null", ignoreCase = true) } ?: "",
        height = heightStr,
        weight = weightStr,
        firstAppearance = biography?.firstAppearance?.takeIf { it != "-" && !it.equals("null", ignoreCase = true) } ?: "",
        placeOfBirth = biography?.placeOfBirth?.takeIf { it != "-" && !it.equals("null", ignoreCase = true) } ?: "",
        occupation = work?.occupation?.takeIf { it != "-" && !it.equals("null", ignoreCase = true) } ?: "",
        groupAffiliation = connections?.groupAffiliation?.takeIf { it != "-" && !it.equals("null", ignoreCase = true) } ?: ""
    )
}

fun generateCdnHeroImageUrl(id: String, name: String): String {
    val cleanSlug = name.lowercase()
        .replace(" - ", "-")
        .replace(" / ", "-")
        .replace(" ", "-")
        .replace("'", "")
        .replace("\"", "")
        .replace(".", "")
        .replace("(", "")
        .replace(")", "")
        .replace("[^a-z0-9-]".toRegex(), "")
        .trim('-')
    return "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/$id-$cleanSlug.jpg"
}