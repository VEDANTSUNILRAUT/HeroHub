package com.vedantraut.herohub.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HeroDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("powerstats")
    val powerstats: PowerstatsDto? = null,
    @SerializedName("biography")
    val biography: BiographyDto? = null,
    @SerializedName("appearance")
    val appearance: AppearanceDto? = null,
    @SerializedName("work")
    val work: WorkDto? = null,
    @SerializedName("connections")
    val connections: ConnectionsDto? = null,
    @SerializedName("image")
    val image: ImageDto? = null
)

data class PowerstatsDto(
    @SerializedName("intelligence")
    val intelligence: String? = null,
    @SerializedName("strength")
    val strength: String? = null,
    @SerializedName("speed")
    val speed: String? = null,
    @SerializedName("durability")
    val durability: String? = null,
    @SerializedName("power")
    val power: String? = null,
    @SerializedName("combat")
    val combat: String? = null
)

data class BiographyDto(
    @SerializedName("full-name")
    val fullName: String? = null,
    @SerializedName("alter-egos")
    val alterEgos: String? = null,
    @SerializedName("aliases")
    val aliases: List<String>? = null,
    @SerializedName("place-of-birth")
    val placeOfBirth: String? = null,
    @SerializedName("first-appearance")
    val firstAppearance: String? = null,
    @SerializedName("publisher")
    val publisher: String? = null,
    @SerializedName("alignment")
    val alignment: String? = null
)

data class AppearanceDto(
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("race")
    val race: String? = null,
    @SerializedName("height")
    val height: List<String>? = null,
    @SerializedName("weight")
    val weight: List<String>? = null,
    @SerializedName("eye-color")
    val eyeColor: String? = null,
    @SerializedName("hair-color")
    val hairColor: String? = null
)

data class WorkDto(
    @SerializedName("occupation")
    val occupation: String? = null,
    @SerializedName("base")
    val base: String? = null
)

data class ConnectionsDto(
    @SerializedName("group-affiliation")
    val groupAffiliation: String? = null,
    @SerializedName("relatives")
    val relatives: String? = null
)

data class ImageDto(
    @SerializedName("url")
    val url: String? = null
)