package com.vedantraut.herohub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vedantraut.herohub.data.local.entity.HeroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroDao {

    @Query("SELECT * FROM heroes ORDER BY powerRating DESC")
    suspend fun getAllHeroes(): List<HeroEntity>

    @Query("SELECT * FROM heroes ORDER BY powerRating DESC")
    fun getAllHeroesFlow(): Flow<List<HeroEntity>>

    @Query("SELECT * FROM heroes WHERE id = :id LIMIT 1")
    suspend fun getHeroById(id: String): HeroEntity?

    @Query("SELECT COUNT(*) FROM heroes")
    suspend fun getHeroCount(): Int

    @Query("""
        SELECT * FROM heroes 
        WHERE name LIKE '%' || :query || '%' 
           OR realName LIKE '%' || :query || '%' 
           OR publisher LIKE '%' || :query || '%' 
           OR groupAffiliation LIKE '%' || :query || '%'
        ORDER BY powerRating DESC
    """)
    suspend fun searchHeroes(query: String): List<HeroEntity>

    @Query("""
        SELECT heroes.* FROM heroes 
        JOIN heroes_fts ON heroes.rowid = heroes_fts.docid 
        WHERE heroes_fts MATCH :query
        ORDER BY heroes.powerRating DESC
    """)
    suspend fun searchHeroesFts(query: String): List<HeroEntity>

    @Query("SELECT * FROM heroes WHERE publisher = :publisher ORDER BY powerRating DESC")
    suspend fun getHeroesByPublisher(publisher: String): List<HeroEntity>

    @Query("SELECT * FROM heroes WHERE alignment = :alignment ORDER BY powerRating DESC")
    suspend fun getHeroesByAlignment(alignment: String): List<HeroEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeroes(heroes: List<HeroEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHero(hero: HeroEntity)

    @Query("DELETE FROM heroes")
    suspend fun clearAllHeroes()
}
