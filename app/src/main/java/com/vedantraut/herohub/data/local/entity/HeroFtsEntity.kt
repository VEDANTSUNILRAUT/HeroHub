package com.vedantraut.herohub.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "heroes_fts")
@Fts4(contentEntity = HeroEntity::class)
data class HeroFtsEntity(
    val name: String,
    val realName: String,
    val publisher: String,
    val occupation: String,
    val groupAffiliation: String
)
