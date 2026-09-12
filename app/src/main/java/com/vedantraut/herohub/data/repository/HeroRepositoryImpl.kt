package com.vedantraut.herohub.data.repository

import android.util.Log
import com.vedantraut.herohub.core.network.NetworkConstants.ACCESS_TOKEN
import com.vedantraut.herohub.data.remote.api.HeroApi
import com.vedantraut.herohub.data.remote.mapper.toDomain
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.repository.HeroRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class HeroRepositoryImpl(
    private val api: HeroApi
) : HeroRepository {

    private var cachedHeroes: List<Hero> = emptyList()

    override suspend fun searchHeroes(name: String): List<Hero> {
        return try {
            val response = api.searchHeroes(
                token = ACCESS_TOKEN,
                name = name
            )
            val results = response.results?.map { it.toDomain() } ?: emptyList()
            if (results.isNotEmpty()) {
                results
            } else {
                getFallbackHeroes().filter {
                    it.name.contains(name, ignoreCase = true) ||
                            it.realName.contains(name, ignoreCase = true)
                }
            }
        } catch (e: Exception) {
            Log.e("HeroRepository", "Error searching heroes: ${e.message}")
            getFallbackHeroes().filter {
                it.name.contains(name, ignoreCase = true) ||
                        it.realName.contains(name, ignoreCase = true)
            }
        }
    }

    override suspend fun getHomeHeroes(): List<Hero> {
        if (cachedHeroes.isNotEmpty()) {
            return cachedHeroes
        }

        val queries = listOf("batman", "spider-man", "iron man", "superman", "thor", "wolverine", "captain america", "deadpool", "joker", "thanos", "flash", "wonder woman", "hulk")

        return try {
            coroutineScope {
                val deferredList = queries.map { query ->
                    async {
                        try {
                            val response = api.searchHeroes(
                                token = ACCESS_TOKEN,
                                name = query
                            )
                            response.results?.map { it.toDomain() } ?: emptyList()
                        } catch (e: Exception) {
                            Log.w("HeroRepository", "Failed fetching for query $query: ${e.message}")
                            emptyList()
                        }
                    }
                }
                val allFetched = deferredList.flatMap { it.await() }
                val distinctHeroes = allFetched.distinctBy { it.id }.filter { it.imageUrl.isNotBlank() }

                val fallback = getFallbackHeroes()
                val merged = if (distinctHeroes.isNotEmpty()) {
                    (fallback + distinctHeroes).distinctBy { it.name.lowercase() }
                } else {
                    fallback
                }
                cachedHeroes = merged
                merged
            }
        } catch (e: Exception) {
            Log.e("HeroRepository", "Error fetching home heroes: ${e.message}")
            val fallback = getFallbackHeroes()
            cachedHeroes = fallback
            fallback
        }
    }

    private fun getFallbackHeroes(): List<Hero> {
        return listOf(
            Hero(
                id = "70",
                name = "Batman",
                realName = "Bruce Wayne",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/70-batman.jpg",
                publisher = "DC Comics",
                alignment = "good",
                powerRating = 88,
                intelligence = 100,
                strength = 26,
                speed = 27,
                durability = 50,
                power = 47,
                combat = 100,
                gender = "Male",
                race = "Human",
                height = "6'2 / 188 cm",
                weight = "210 lb / 95 kg",
                firstAppearance = "Detective Comics #27",
                placeOfBirth = "Gotham City",
                occupation = "Businessman, Vigilante",
                groupAffiliation = "Justice League, Batman Family"
            ),
            Hero(
                id = "620",
                name = "Spider-Man",
                realName = "Peter Parker",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/620-spider-man.jpg",
                publisher = "Marvel Comics",
                alignment = "good",
                powerRating = 90,
                intelligence = 90,
                strength = 55,
                speed = 67,
                durability = 75,
                power = 74,
                combat = 85,
                gender = "Male",
                race = "Human",
                height = "5'10 / 178 cm",
                weight = "165 lb / 75 kg",
                firstAppearance = "Amazing Fantasy #15",
                placeOfBirth = "Queens, New York",
                occupation = "Photographer, Scientist, Vigilante",
                groupAffiliation = "Avengers, Fantastic Four"
            ),
            Hero(
                id = "346",
                name = "Iron Man",
                realName = "Tony Stark",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/346-iron-man.jpg",
                publisher = "Marvel Comics",
                alignment = "good",
                powerRating = 93,
                intelligence = 100,
                strength = 85,
                speed = 58,
                durability = 85,
                power = 100,
                combat = 64,
                gender = "Male",
                race = "Human",
                height = "6'6 / 198 cm",
                weight = "425 lb / 191 kg",
                firstAppearance = "Tales of Suspense #39",
                placeOfBirth = "Long Island, New York",
                occupation = "Inventor, Industrialist, CEO",
                groupAffiliation = "Avengers, Illuminati, Stark Industries"
            ),
            Hero(
                id = "644",
                name = "Superman",
                realName = "Clark Kent (Kal-El)",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/644-superman.jpg",
                publisher = "DC Comics",
                alignment = "good",
                powerRating = 98,
                intelligence = 94,
                strength = 100,
                speed = 100,
                durability = 100,
                power = 100,
                combat = 85,
                gender = "Male",
                race = "Kryptonian",
                height = "6'3 / 191 cm",
                weight = "225 lb / 101 kg",
                firstAppearance = "Action Comics #1",
                placeOfBirth = "Krypton",
                occupation = "Reporter, Daily Planet",
                groupAffiliation = "Justice League"
            ),
            Hero(
                id = "659",
                name = "Thor",
                realName = "Thor Odinson",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/659-thor.jpg",
                publisher = "Marvel Comics",
                alignment = "good",
                powerRating = 95,
                intelligence = 69,
                strength = 100,
                speed = 83,
                durability = 100,
                power = 100,
                combat = 100,
                gender = "Male",
                race = "Asgardian",
                height = "6'6 / 198 cm",
                weight = "640 lb / 288 kg",
                firstAppearance = "Journey into Mystery #83",
                placeOfBirth = "Asgard",
                occupation = "God of Thunder, King of Asgard",
                groupAffiliation = "Avengers, Asgardians"
            ),
            Hero(
                id = "720",
                name = "Wonder Woman",
                realName = "Diana Prince",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/720-wonder-woman.jpg",
                publisher = "DC Comics",
                alignment = "good",
                powerRating = 92,
                intelligence = 88,
                strength = 100,
                speed = 79,
                durability = 100,
                power = 100,
                combat = 100,
                gender = "Female",
                race = "Amazon",
                height = "6'0 / 183 cm",
                weight = "165 lb / 74 kg",
                firstAppearance = "All Star Comics #8",
                placeOfBirth = "Themyscira",
                occupation = "Amazon Princess, Warrior",
                groupAffiliation = "Justice League, Amazons"
            ),
            Hero(
                id = "717",
                name = "Wolverine",
                realName = "Logan (James Howlett)",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/717-wolverine.jpg",
                publisher = "Marvel Comics",
                alignment = "good",
                powerRating = 87,
                intelligence = 63,
                strength = 32,
                speed = 50,
                durability = 100,
                power = 89,
                combat = 100,
                gender = "Male",
                race = "Mutant",
                height = "5'3 / 160 cm",
                weight = "300 lb / 135 kg",
                firstAppearance = "Incredible Hulk #180",
                placeOfBirth = "Alberta, Canada",
                occupation = "Adventurer, Instructor",
                groupAffiliation = "X-Men, Avengers, Alpha Flight"
            ),
            Hero(
                id = "149",
                name = "Captain America",
                realName = "Steve Rogers",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/149-captain-america.jpg",
                publisher = "Marvel Comics",
                alignment = "good",
                powerRating = 85,
                intelligence = 69,
                strength = 19,
                speed = 38,
                durability = 55,
                power = 60,
                combat = 100,
                gender = "Male",
                race = "Human",
                height = "6'2 / 188 cm",
                weight = "240 lb / 108 kg",
                firstAppearance = "Captain America Comics #1",
                placeOfBirth = "Manhattan, New York",
                occupation = "Soldier, Hero",
                groupAffiliation = "Avengers, S.H.I.E.L.D., Invaders"
            ),
            Hero(
                id = "213",
                name = "Deadpool",
                realName = "Wade Wilson",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/213-deadpool.jpg",
                publisher = "Marvel Comics",
                alignment = "neutral",
                powerRating = 86,
                intelligence = 69,
                strength = 28,
                speed = 50,
                durability = 100,
                power = 100,
                combat = 100,
                gender = "Male",
                race = "Mutant",
                height = "6'2 / 188 cm",
                weight = "210 lb / 95 kg",
                firstAppearance = "New Mutants #98",
                placeOfBirth = "Canada",
                occupation = "Mercenary, Assassin",
                groupAffiliation = "X-Force, Mercs for Money, Deadpool Corps"
            ),
            Hero(
                id = "370",
                name = "Joker",
                realName = "Unknown (Jack Napier / Arthur Fleck)",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/370-joker.jpg",
                publisher = "DC Comics",
                alignment = "bad",
                powerRating = 78,
                intelligence = 100,
                strength = 10,
                speed = 12,
                durability = 60,
                power = 43,
                combat = 70,
                gender = "Male",
                race = "Human",
                height = "6'5 / 196 cm",
                weight = "192 lb / 86 kg",
                firstAppearance = "Batman #1",
                placeOfBirth = "Gotham City",
                occupation = "Criminal Mastermind",
                groupAffiliation = "Injustice League, Legion of Doom"
            ),
            Hero(
                id = "655",
                name = "Thanos",
                realName = "Thanos",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/655-thanos.jpg",
                publisher = "Marvel Comics",
                alignment = "bad",
                powerRating = 99,
                intelligence = 100,
                strength = 100,
                speed = 33,
                durability = 100,
                power = 100,
                combat = 80,
                gender = "Male",
                race = "Eternal",
                height = "6'7 / 201 cm",
                weight = "985 lb / 443 kg",
                firstAppearance = "Iron Man #55",
                placeOfBirth = "Titan",
                occupation = "Conqueror, Warlord",
                groupAffiliation = "Black Order, Infinity Watch"
            ),
            Hero(
                id = "263",
                name = "Flash",
                realName = "Barry Allen",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/263-flash.jpg",
                publisher = "DC Comics",
                alignment = "good",
                powerRating = 91,
                intelligence = 88,
                strength = 48,
                speed = 100,
                durability = 60,
                power = 100,
                combat = 60,
                gender = "Male",
                race = "Human",
                height = "6'0 / 183 cm",
                weight = "195 lb / 88 kg",
                firstAppearance = "Showcase #4",
                placeOfBirth = "Fallville, Iowa",
                occupation = "Forensic Scientist",
                groupAffiliation = "Justice League, Flash Family"
            ),
            Hero(
                id = "332",
                name = "Hulk",
                realName = "Bruce Banner",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/332-hulk.jpg",
                publisher = "Marvel Comics",
                alignment = "good",
                powerRating = 96,
                intelligence = 88,
                strength = 100,
                speed = 63,
                durability = 100,
                power = 98,
                combat = 85,
                gender = "Male",
                race = "Human / Radiation",
                height = "8'0 / 244 cm",
                weight = "1400 lb / 630 kg",
                firstAppearance = "Incredible Hulk #1",
                placeOfBirth = "Dayton, Ohio",
                occupation = "Nuclear Physicist, Hero",
                groupAffiliation = "Avengers, Defenders, Warbound"
            ),
            Hero(
                id = "226",
                name = "Doctor Strange",
                realName = "Stephen Strange",
                imageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/226-doctor-strange.jpg",
                publisher = "Marvel Comics",
                alignment = "good",
                powerRating = 97,
                intelligence = 100,
                strength = 10,
                speed = 12,
                durability = 84,
                power = 100,
                combat = 60,
                gender = "Male",
                race = "Human",
                height = "6'2 / 188 cm",
                weight = "180 lb / 81 kg",
                firstAppearance = "Strange Tales #110",
                placeOfBirth = "Philadelphia, Pennsylvania",
                occupation = "Sorcerer Supreme, Neurosurgeon",
                groupAffiliation = "Avengers, Defenders, Illuminati"
            )
        )
    }
}
