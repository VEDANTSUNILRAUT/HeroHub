package com.vedantraut.herohub.data.local

import com.vedantraut.herohub.data.local.catalog.CuratedHeroCatalog
import com.vedantraut.herohub.data.local.mapper.toDomain
import com.vedantraut.herohub.data.local.mapper.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CuratedHeroCatalogTest {

    @Test
    fun `curated catalog contains over 50 heroes`() {
        val heroes = CuratedHeroCatalog.getCuratedHeroes()
        assertTrue("Catalog should have 50+ heroes, found ${heroes.size}", heroes.size >= 50)
    }

    @Test
    fun `curated catalog contains all required fan favorites`() {
        val heroes = CuratedHeroCatalog.getCuratedHeroes()
        val heroNames = heroes.map { it.name.lowercase() }

        val requiredHeroes = listOf(
            "doctor doom",
            "omni-man",
            "homelander",
            "black panther",
            "shazam",
            "green lantern",
            "magneto",
            "venom",
            "invincible"
        )

        for (required in requiredHeroes) {
            assertTrue("Expected to find $required in catalog", heroNames.any { it.contains(required) })
        }
    }

    @Test
    fun `hero to entity and back mapping preserves data`() {
        val original = CuratedHeroCatalog.getCuratedHeroes().first()
        val entity = original.toEntity()
        val mappedBack = entity.toDomain()

        assertEquals(original.id, mappedBack.id)
        assertEquals(original.name, mappedBack.name)
        assertEquals(original.intelligence, mappedBack.intelligence)
        assertEquals(original.strength, mappedBack.strength)
        assertEquals(original.speed, mappedBack.speed)
        assertEquals(original.durability, mappedBack.durability)
        assertEquals(original.power, mappedBack.power)
        assertEquals(original.combat, mappedBack.combat)
        assertEquals(original.publisher, mappedBack.publisher)
    }
}
