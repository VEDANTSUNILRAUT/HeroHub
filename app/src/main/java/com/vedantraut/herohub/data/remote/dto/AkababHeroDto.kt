package com.vedantraut.herohub.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.vedantraut.herohub.domain.model.Hero
import kotlin.math.roundToInt

data class AkababHeroDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("powerstats") val powerstats: AkababPowerstatsDto?,
    @SerializedName("appearance") val appearance: AkababAppearanceDto?,
    @SerializedName("biography") val biography: AkababBiographyDto?,
    @SerializedName("work") val work: AkababWorkDto?,
    @SerializedName("connections") val connections: AkababConnectionsDto?,
    @SerializedName("images") val images: AkababImagesDto?
)

data class AkababPowerstatsDto(
    @SerializedName("intelligence") val intelligence: Int?,
    @SerializedName("strength") val strength: Int?,
    @SerializedName("speed") val speed: Int?,
    @SerializedName("durability") val durability: Int?,
    @SerializedName("power") val power: Int?,
    @SerializedName("combat") val combat: Int?
)

data class AkababAppearanceDto(
    @SerializedName("gender") val gender: String?,
    @SerializedName("race") val race: String?,
    @SerializedName("height") val height: List<String>?,
    @SerializedName("weight") val weight: List<String>?
)

data class AkababBiographyDto(
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("alterEgos") val alterEgos: String?,
    @SerializedName("aliases") val aliases: List<String>?,
    @SerializedName("placeOfBirth") val placeOfBirth: String?,
    @SerializedName("firstAppearance") val firstAppearance: String?,
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("alignment") val alignment: String?
)

data class AkababWorkDto(
    @SerializedName("occupation") val occupation: String?,
    @SerializedName("base") val base: String?
)

data class AkababConnectionsDto(
    @SerializedName("groupAffiliation") val groupAffiliation: String?,
    @SerializedName("relatives") val relatives: String?
)

data class AkababImagesDto(
    @SerializedName("xs") val xs: String?,
    @SerializedName("sm") val sm: String?,
    @SerializedName("md") val md: String?,
    @SerializedName("lg") val lg: String?
)

fun AkababHeroDto.toDomain(): Hero {
    val intel = powerstats?.intelligence ?: 0
    val str = powerstats?.strength ?: 0
    val spd = powerstats?.speed ?: 0
    val dur = powerstats?.durability ?: 0
    val pwr = powerstats?.power ?: 0
    val com = powerstats?.combat ?: 0

    val statCount = listOf(intel, str, spd, dur, pwr, com).count { it > 0 }
    val avg = if (statCount > 0) {
        ((intel + str + spd + dur + pwr + com) / 6.0).roundToInt().coerceIn(10, 100)
    } else {
        50
    }

    val img = images?.lg ?: images?.md ?: images?.sm ?: ""
    val cleanImg = img.replace("cdn.jsdelivr.net", "fastly.jsdelivr.net")

    return Hero(
        id = id.toString(),
        name = name ?: "Unknown Hero",
        realName = biography?.fullName ?: "",
        imageUrl = cleanImg,
        publisher = biography?.publisher ?: "Unknown",
        alignment = biography?.alignment ?: "neutral",
        powerRating = avg,
        intelligence = intel,
        strength = str,
        speed = spd,
        durability = dur,
        power = pwr,
        combat = com,
        gender = appearance?.gender ?: "",
        race = appearance?.race ?: "",
        height = appearance?.height?.joinToString(" / ") ?: "",
        weight = appearance?.weight?.joinToString(" / ") ?: "",
        firstAppearance = biography?.firstAppearance ?: "",
        placeOfBirth = biography?.placeOfBirth ?: "",
        occupation = work?.occupation ?: "",
        groupAffiliation = connections?.groupAffiliation ?: ""
    )
}
