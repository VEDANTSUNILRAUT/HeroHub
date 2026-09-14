package com.vedantraut.herohub.domain.model.battle

import androidx.compose.runtime.Immutable
import com.vedantraut.herohub.domain.model.Hero

enum class BattleCategory(
    val displayName: String,
    val emoji: String,
    val description: String
) {
    INTELLIGENCE("Intelligence", "🧠", "Tactical cognition, planning, deduction, and scientific expertise"),
    STRENGTH("Strength", "💪", "Raw physical lifting, striking force, and kinetic devastation"),
    SPEED("Speed", "⚡", "Movement velocity, acceleration, and combat reaction time"),
    DURABILITY("Durability", "🛡️", "Physical resilience, invulnerability, and regeneration capacity"),
    POWER("Power", "🔮", "Energy projection, exotic abilities, and destructive potential"),
    COMBAT("Combat", "🥋", "Martial arts fluency, tactical warfare, and hand-to-hand mastery")
}

enum class PowerTier(
    val title: String,
    val description: String,
    val badgeColorHex: Long
) {
    STREET("Street Tier", "Peak-human to low superhuman; urban vigilantes", 0xFF4CAF50),
    CITY("City Tier", "City-block to metropolitan destructive scale", 0xFF2196F3),
    PLANETARY("Planetary Tier", "Continental to planet-shattering power output", 0xFFFF9800),
    COSMIC("Cosmic Tier", "God-like, reality-warping, or universal magnitude", 0xFF9C27B0);

    companion object {
        fun fromAverageStat(avg: Int): PowerTier {
            return when {
                avg >= 88 -> COSMIC
                avg >= 70 -> PLANETARY
                avg >= 45 -> CITY
                else -> STREET
            }
        }
    }
}

enum class BattleScenario(
    val title: String,
    val shortLabel: String,
    val description: String
) {
    STANDARD(
        title = "Standard Encounter",
        shortLabel = "Standard",
        description = "Random encounter in a neutral arena. Both fighters in-character (morals on), no prior prep time."
    ),
    PREP_TIME_24H(
        title = "24-Hour Prep Time",
        shortLabel = "24h Prep",
        description = "Both combatants receive 24 hours of Intel, resource access, and tactical planning before clash."
    ),
    BLOODLUSTED(
        title = "Bloodlusted / Morals Off",
        shortLabel = "Bloodlusted",
        description = "Fighters remove all moral inhibitions, using 100% lethality, peak speed blitz, and zero hesitation."
    )
}

@Immutable
data class CategoryComparison(
    val category: BattleCategory,
    val fighter1Raw: Int,
    val fighter2Raw: Int,
    val fighter1Effective: Int,
    val fighter2Effective: Int,
    val delta: Int, // effective1 - effective2
    val winnerSlot: Int?, // 1, 2, or null for tie
    val qualitativeAnalysis: String
)

@Immutable
data class BattleResult(
    val fighter1: Hero,
    val fighter2: Hero,
    val scenario: BattleScenario,
    val fighter1Tier: PowerTier,
    val fighter2Tier: PowerTier,
    val comparisons: List<CategoryComparison>,
    val fighter1Wins: Int,
    val fighter2Wins: Int,
    val ties: Int,
    val winnerSlot: Int?, // 1, 2, or null
    val winnerHero: Hero?,
    val winProbability: Int, // 50 to 99
    val decidingFactor: String,
    val keyVulnerability: String,
    val tacticalSummary: String
)
