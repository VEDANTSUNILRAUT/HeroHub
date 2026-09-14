package com.vedantraut.herohub.presentation.categories

import androidx.compose.runtime.Immutable

enum class CategoryGroup(val displayName: String) {
    ALL("All Heroes (700+)"),
    PUBLISHERS("Universes & Publishers"),
    ALIGNMENTS("Alignments & Roles"),
    POWER_CLASSES("Power Classes"),
    ORIGIN("Origins & Species")
}

@Immutable
data class SubcategoryItem(
    val id: String,
    val title: String,
    val tag: String
)

@Immutable
data class CategoryItem(
    val id: String,
    val title: String,
    val description: String,
    val bannerImageUrl: String,
    val group: CategoryGroup,
    val estimatedHeroCount: Int,
    val subcategories: List<SubcategoryItem>
)

enum class CategorySortOrder(val displayName: String) {
    POWER_DESC("Highest Power"),
    POWER_ASC("Lowest Power"),
    NAME_ASC("Name (A–Z)"),
    NAME_DESC("Name (Z–A)")
}

enum class CategoryViewMode {
    GRID,
    LIST
}

object CategoryPresets {
    val defaultCategories = listOf(
        // Group 0: Complete Global Registry
        CategoryItem(
            id = "all_heroes_global",
            title = "All Heroes & Villains (700+)",
            description = "Explore the complete global registry of 700+ characters extracted live from the API database",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/644-superman.jpg",
            group = CategoryGroup.ALL,
            estimatedHeroCount = 731,
            subcategories = listOf(
                SubcategoryItem("all_all", "All 700+", "all"),
                SubcategoryItem("all_marvel", "Marvel (280+)", "Marvel"),
                SubcategoryItem("all_dc", "DC Comics (220+)", "DC"),
                SubcategoryItem("all_indie", "Indie & Others (100+)", "Indie"),
                SubcategoryItem("all_heroes", "Superheroes", "good"),
                SubcategoryItem("all_villains", "Villains", "bad")
            )
        ),

        // Group 1: Publishers & Universes
        CategoryItem(
            id = "marvel",
            title = "Marvel Comics",
            description = "Earth-616 and the multiverse: Avengers, X-Men, Spider-Verse & beyond",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/620-spider-man.jpg",
            group = CategoryGroup.PUBLISHERS,
            estimatedHeroCount = 280,
            subcategories = listOf(
                SubcategoryItem("all_marvel", "All Marvel", "Marvel"),
                SubcategoryItem("avengers", "Avengers", "Avengers"),
                SubcategoryItem("xmen", "X-Men", "X-Men"),
                SubcategoryItem("illuminati", "Illuminati", "Illuminati"),
                SubcategoryItem("cosmic_marvel", "Cosmic Marvel", "Cosmic")
            )
        ),
        CategoryItem(
            id = "dc",
            title = "DC Comics",
            description = "Gods among us: Justice League, Gotham Rogues, and the Multiverse",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/70-batman.jpg",
            group = CategoryGroup.PUBLISHERS,
            estimatedHeroCount = 220,
            subcategories = listOf(
                SubcategoryItem("all_dc", "All DC", "DC Comics"),
                SubcategoryItem("justice_league", "Justice League", "Justice League"),
                SubcategoryItem("bat_family", "Bat-Family", "Batman"),
                SubcategoryItem("flash_family", "Flash Family", "Flash"),
                SubcategoryItem("amazons", "Amazons", "Amazons")
            )
        ),
        CategoryItem(
            id = "indie",
            title = "Dark Horse & Indie",
            description = "Hellboy, Spawn, Invincible, The Boys, and independent comic legends",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/213-deadpool.jpg",
            group = CategoryGroup.PUBLISHERS,
            estimatedHeroCount = 45,
            subcategories = listOf(
                SubcategoryItem("all_indie", "All Indie", "Indie"),
                SubcategoryItem("dark_horse", "Dark Horse", "Dark Horse"),
                SubcategoryItem("image", "Image Comics", "Image"),
                SubcategoryItem("anti_heroes", "Anti-Hero Squad", "Anti-Hero")
            )
        ),

        // Group 2: Alignments & Roles
        CategoryItem(
            id = "heroes",
            title = "Superheroes",
            description = "Champions of justice protecting humanity against impossible odds",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/644-superman.jpg",
            group = CategoryGroup.ALIGNMENTS,
            estimatedHeroCount = 350,
            subcategories = listOf(
                SubcategoryItem("all_heroes", "All Heroes", "good"),
                SubcategoryItem("god_heroes", "Godly Protectors", "god_heroes"),
                SubcategoryItem("street_heroes", "Street Vigilantes", "street_heroes")
            )
        ),
        CategoryItem(
            id = "villains",
            title = "Supervillains",
            description = "Masterminds, conquerors, and chaotic forces bent on domination",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/370-joker.jpg",
            group = CategoryGroup.ALIGNMENTS,
            estimatedHeroCount = 180,
            subcategories = listOf(
                SubcategoryItem("all_villains", "All Villains", "bad"),
                SubcategoryItem("warlords", "Cosmic Warlords", "warlords"),
                SubcategoryItem("psychopaths", "Arkham Rogues", "psychopaths")
            )
        ),
        CategoryItem(
            id = "anti_heroes",
            title = "Anti-Heroes & Rogues",
            description = "Vigilantes who walk the thin morally grey line between light and shadow",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/717-wolverine.jpg",
            group = CategoryGroup.ALIGNMENTS,
            estimatedHeroCount = 65,
            subcategories = listOf(
                SubcategoryItem("all_neutral", "All Neutral", "neutral"),
                SubcategoryItem("mercenaries", "Mercenaries", "mercenary"),
                SubcategoryItem("mutant_rebels", "Mutant Rebels", "mutant")
            )
        ),

        // Group 3: Power Classes
        CategoryItem(
            id = "god_tier",
            title = "Cosmic & God-Tier",
            description = "Beings of omnipotence with power ratings 90+: Thor, Superman, Thanos, Strange",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/659-thor.jpg",
            group = CategoryGroup.POWER_CLASSES,
            estimatedHeroCount = 42,
            subcategories = listOf(
                SubcategoryItem("power_95_plus", "Power 95+", "power_95"),
                SubcategoryItem("power_90_94", "Power 90–94", "power_90")
            )
        ),
        CategoryItem(
            id = "heavyweights",
            title = "Metahuman Heavyweights",
            description = "Super soldiers, mutated powerhouses, and tactical dynamos (Power 80–89)",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/332-hulk.jpg",
            group = CategoryGroup.POWER_CLASSES,
            estimatedHeroCount = 110,
            subcategories = listOf(
                SubcategoryItem("power_85_89", "Power 85–89", "power_85"),
                SubcategoryItem("power_80_84", "Power 80–84", "power_80")
            )
        ),
        CategoryItem(
            id = "street_tech",
            title = "Martial & High-Tech",
            description = "Billionaire armor, peak human conditioning, martial prowess, and tactical minds",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/346-iron-man.jpg",
            group = CategoryGroup.POWER_CLASSES,
            estimatedHeroCount = 190,
            subcategories = listOf(
                SubcategoryItem("tech_geniuses", "Tech Geniuses", "tech"),
                SubcategoryItem("street_martial", "Martial Artists", "martial")
            )
        ),

        // Group 4: Origins & Species
        CategoryItem(
            id = "mutants",
            title = "Mutants & Homo Superior",
            description = "Born with the X-gene: telepaths, energy manipulators, and biological wonders",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/717-wolverine.jpg",
            group = CategoryGroup.ORIGIN,
            estimatedHeroCount = 95,
            subcategories = listOf(
                SubcategoryItem("omega_mutants", "Omega Level", "omega"),
                SubcategoryItem("x_men", "Xavier Students", "xmen")
            )
        ),
        CategoryItem(
            id = "aliens_gods",
            title = "Aliens, Gods & Mythic",
            description = "Asgardians, Kryptonians, Olympians, Eternals, and cosmic entities",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/655-thanos.jpg",
            group = CategoryGroup.ORIGIN,
            estimatedHeroCount = 78,
            subcategories = listOf(
                SubcategoryItem("kryptonians", "Kryptonians", "krypton"),
                SubcategoryItem("asgardians", "Asgardians", "asgard"),
                SubcategoryItem("eternals", "Eternals & Titans", "titan")
            )
        ),
        CategoryItem(
            id = "human_peak",
            title = "Peak Humans & Enhanced",
            description = "Serum-injected super soldiers, master detectives, and peak athletes",
            bannerImageUrl = "https://fastly.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api/images/lg/149-captain-america.jpg",
            group = CategoryGroup.ORIGIN,
            estimatedHeroCount = 140,
            subcategories = listOf(
                SubcategoryItem("super_soldiers", "Super Soldiers", "soldier"),
                SubcategoryItem("detectives", "Master Detectives", "detective")
            )
        )
    )
}
