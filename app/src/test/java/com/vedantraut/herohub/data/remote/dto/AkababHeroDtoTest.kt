package com.vedantraut.herohub.data.remote.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AkababHeroDtoTest {

    @Test
    fun `akabab hero dto mapping calculates power rating and maps all fields`() {
        val dto = AkababHeroDto(
            id = 1,
            name = "A-Bomb",
            slug = "1-a-bomb",
            powerstats = AkababPowerstatsDto(
                intelligence = 38,
                strength = 100,
                speed = 17,
                durability = 80,
                power = 24,
                combat = 64
            ),
            appearance = AkababAppearanceDto(
                gender = "Male",
                race = "Human",
                height = listOf("6'8", "203 cm"),
                weight = listOf("980 lb", "441 kg")
            ),
            biography = AkababBiographyDto(
                fullName = "Richard Milhouse Jones",
                alterEgos = "No alter egos found.",
                aliases = listOf("Rick Jones"),
                placeOfBirth = "Scarsdale, Arizona",
                firstAppearance = "Hulk Vol 2 #2",
                publisher = "Marvel Comics",
                alignment = "good"
            ),
            work = AkababWorkDto(
                occupation = "Musician, adventurer",
                base = "-"
            ),
            connections = AkababConnectionsDto(
                groupAffiliation = "Hulk Family, Avengers",
                relatives = null
            ),
            images = AkababImagesDto(
                xs = null,
                sm = null,
                md = null,
                lg = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/1-a-bomb.jpg"
            )
        )

        val domain = dto.toDomain()

        assertEquals("1", domain.id)
        assertEquals("A-Bomb", domain.name)
        assertEquals("Richard Milhouse Jones", domain.realName)
        assertEquals("Marvel Comics", domain.publisher)
        assertEquals("good", domain.alignment)
        assertEquals(38, domain.intelligence)
        assertEquals(100, domain.strength)
        assertEquals(17, domain.speed)
        assertEquals(80, domain.durability)
        assertEquals(24, domain.power)
        assertEquals(64, domain.combat)
        assertTrue(domain.powerRating > 0)
        assertEquals("6'8 / 203 cm", domain.height)
        assertEquals("980 lb / 441 kg", domain.weight)
        assertTrue(domain.imageUrl.contains("fastly.jsdelivr.net"))
    }
}
