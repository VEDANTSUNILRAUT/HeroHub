package com.vedantraut.herohub.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vedantraut.herohub.data.local.dao.HeroDao
import com.vedantraut.herohub.data.local.entity.HeroEntity
import com.vedantraut.herohub.data.local.entity.HeroFtsEntity

@Database(
    entities = [HeroEntity::class, HeroFtsEntity::class],
    version = 2,
    exportSchema = false
)
abstract class HeroDatabase : RoomDatabase() {
    abstract fun heroDao(): HeroDao
}
