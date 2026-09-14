package com.vedantraut.herohub.domain.battle

import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.model.battle.BattleCategory
import com.vedantraut.herohub.domain.model.battle.BattleResult
import com.vedantraut.herohub.domain.model.battle.BattleScenario
import com.vedantraut.herohub.domain.model.battle.CategoryComparison
import com.vedantraut.herohub.domain.model.battle.PowerTier
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class BattleEngine {

    fun evaluateBattle(
        fighter1: Hero,
        fighter2: Hero,
        scenario: BattleScenario = BattleScenario.STANDARD
    ): BattleResult {
        val f1Avg = calculateAverageStat(fighter1)
        val f2Avg = calculateAverageStat(fighter2)
        val tier1 = PowerTier.fromAverageStat(f1Avg)
        val tier2 = PowerTier.fromAverageStat(f2Avg)

        val comparisons = BattleCategory.entries.map { category ->
            evaluateCategory(category, fighter1, fighter2, scenario)
        }

        var f1Wins = 0
        var f2Wins = 0
        var ties = 0

        var f1WeightedScore = 0.0
        var f2WeightedScore = 0.0

        comparisons.forEach { comp ->
            val weight = getCategoryWeight(comp.category, scenario)
            f1WeightedScore += comp.fighter1Effective * weight
            f2WeightedScore += comp.fighter2Effective * weight

            when (comp.winnerSlot) {
                1 -> f1Wins++
                2 -> f2Wins++
                else -> ties++
            }
        }

        // Tier differential multiplier: higher tier combatants have defensive / scale multipliers
        val tierDiff = tier1.ordinal - tier2.ordinal
        if (tierDiff > 0) {
            f1WeightedScore *= (1.0 + (tierDiff * 0.25))
        } else if (tierDiff < 0) {
            f2WeightedScore *= (1.0 + (abs(tierDiff) * 0.25))
        }

        val totalScore = f1WeightedScore + f2WeightedScore
        val winnerSlot: Int?
        val winnerHero: Hero?
        val winProbability: Int

        val scoreDiff = f1WeightedScore - f2WeightedScore
        val marginPercent = if (totalScore > 0) (abs(scoreDiff) / totalScore) else 0.0

        if (abs(scoreDiff) < 2.0 && f1Wins == f2Wins) {
            winnerSlot = null
            winnerHero = null
            winProbability = 50
        } else if (scoreDiff > 0) {
            winnerSlot = 1
            winnerHero = fighter1
            winProbability = min(99, (53 + (marginPercent * 60)).roundToInt())
        } else {
            winnerSlot = 2
            winnerHero = fighter2
            winProbability = min(99, (53 + (marginPercent * 60)).roundToInt())
        }

        val (decidingFactor, keyVulnerability, tacticalSummary) = generateTacticalNarrative(
            winner = winnerHero,
            loser = if (winnerSlot == 1) fighter2 else fighter1,
            comparisons = comparisons,
            scenario = scenario,
            tierDiff = tierDiff,
            winProb = winProbability
        )

        return BattleResult(
            fighter1 = fighter1,
            fighter2 = fighter2,
            scenario = scenario,
            fighter1Tier = tier1,
            fighter2Tier = tier2,
            comparisons = comparisons,
            fighter1Wins = f1Wins,
            fighter2Wins = f2Wins,
            ties = ties,
            winnerSlot = winnerSlot,
            winnerHero = winnerHero,
            winProbability = winProbability,
            decidingFactor = decidingFactor,
            keyVulnerability = keyVulnerability,
            tacticalSummary = tacticalSummary
        )
    }

    private fun evaluateCategory(
        category: BattleCategory,
        h1: Hero,
        h2: Hero,
        scenario: BattleScenario
    ): CategoryComparison {
        val raw1 = getRawStat(h1, category)
        val raw2 = getRawStat(h2, category)

        val eff1 = calculateEffectiveStat(h1, category, raw1, scenario)
        val eff2 = calculateEffectiveStat(h2, category, raw2, scenario)

        val delta = eff1 - eff2
        val winnerSlot = when {
            delta > 0 -> 1
            delta < 0 -> 2
            else -> null
        }

        val desc = when {
            abs(delta) >= 50 -> "Devastating dominance (+${abs(delta)})"
            abs(delta) >= 25 -> "Clear distinct advantage (+${abs(delta)})"
            abs(delta) >= 10 -> "Noticeable combat edge (+${abs(delta)})"
            abs(delta) > 0 -> "Slight contested advantage (+${abs(delta)})"
            else -> "Perfect parity (Deadlock)"
        }

        return CategoryComparison(
            category = category,
            fighter1Raw = raw1,
            fighter2Raw = raw2,
            fighter1Effective = eff1,
            fighter2Effective = eff2,
            delta = delta,
            winnerSlot = winnerSlot,
            qualitativeAnalysis = desc
        )
    }

    private fun getRawStat(hero: Hero, category: BattleCategory): Int {
        return when (category) {
            BattleCategory.INTELLIGENCE -> hero.intelligence
            BattleCategory.STRENGTH -> hero.strength
            BattleCategory.SPEED -> hero.speed
            BattleCategory.DURABILITY -> hero.durability
            BattleCategory.POWER -> hero.power
            BattleCategory.COMBAT -> hero.combat
        }.coerceIn(0, 100)
    }

    private fun calculateEffectiveStat(
        hero: Hero,
        category: BattleCategory,
        raw: Int,
        scenario: BattleScenario
    ): Int {
        var stat = raw

        when (scenario) {
            BattleScenario.PREP_TIME_24H -> {
                // High tactical intelligence dramatically enhances prep effectiveness
                if (hero.intelligence >= 85) {
                    when (category) {
                        BattleCategory.INTELLIGENCE -> stat = (stat + 5).coerceAtMost(100)
                        BattleCategory.COMBAT -> stat = (stat + 12).coerceAtMost(100)
                        BattleCategory.POWER -> stat = (stat + 15).coerceAtMost(100) // Access to custom gadgets/suits
                        BattleCategory.DURABILITY -> stat = (stat + 10).coerceAtMost(100)
                        else -> {}
                    }
                } else if (hero.intelligence >= 65) {
                    when (category) {
                        BattleCategory.COMBAT -> stat = (stat + 5).coerceAtMost(100)
                        BattleCategory.POWER -> stat = (stat + 5).coerceAtMost(100)
                        else -> {}
                    }
                }
            }
            BattleScenario.BLOODLUSTED -> {
                // Maximum aggression and removing moral inhibitions multiplies Speed and Power
                when (category) {
                    BattleCategory.SPEED -> stat = (stat + 12).coerceAtMost(100) // Speed-blitz factor
                    BattleCategory.POWER -> stat = (stat + 12).coerceAtMost(100) // Full output lethality
                    BattleCategory.STRENGTH -> stat = (stat + 8).coerceAtMost(100)
                    else -> {}
                }
            }
            BattleScenario.STANDARD -> {
                // Standard in-character values
            }
        }

        return stat.coerceIn(0, 100)
    }

    private fun getCategoryWeight(category: BattleCategory, scenario: BattleScenario): Double {
        return when (scenario) {
            BattleScenario.PREP_TIME_24H -> when (category) {
                BattleCategory.INTELLIGENCE -> 1.5
                BattleCategory.POWER -> 1.3
                BattleCategory.COMBAT -> 1.2
                else -> 1.0
            }
            BattleScenario.BLOODLUSTED -> when (category) {
                BattleCategory.SPEED -> 1.6
                BattleCategory.POWER -> 1.4
                BattleCategory.STRENGTH -> 1.3
                BattleCategory.DURABILITY -> 1.2
                else -> 0.9
            }
            BattleScenario.STANDARD -> when (category) {
                BattleCategory.DURABILITY -> 1.25
                BattleCategory.COMBAT -> 1.2
                BattleCategory.POWER -> 1.2
                BattleCategory.SPEED -> 1.15
                BattleCategory.STRENGTH -> 1.1
                BattleCategory.INTELLIGENCE -> 1.05
            }
        }
    }

    private fun calculateAverageStat(hero: Hero): Int {
        val sum = hero.intelligence + hero.strength + hero.speed +
                hero.durability + hero.power + hero.combat
        return (sum / 6.0).roundToInt()
    }

    private fun generateTacticalNarrative(
        winner: Hero?,
        loser: Hero,
        comparisons: List<CategoryComparison>,
        scenario: BattleScenario,
        tierDiff: Int,
        winProb: Int
    ): Triple<String, String, String> {
        if (winner == null) {
            return Triple(
                "Dead heat stalemate",
                "Both combatants counter each other's offensive outputs perfectly",
                "Under $scenario conditions, neither fighter holds a decisive category edge. Any victory would rely on external variables or attrition."
            )
        }

        // Find primary strengths of winner
        val winnerSlot = if (winner.id == comparisons.first().winnerSlot.toString()) 1 else 2
        val majorLeads = comparisons.filter { it.winnerSlot == winnerSlot }.sortedByDescending { abs(it.delta) }
        val primaryEdge = majorLeads.firstOrNull()?.category ?: BattleCategory.COMBAT

        val decidingFactor = when (primaryEdge) {
            BattleCategory.INTELLIGENCE -> "Superior tactical foresight, contingency architecture, and exploitation of enemy habits."
            BattleCategory.STRENGTH -> "Irresistible physical striking supremacy and overwhelming kinetic force."
            BattleCategory.SPEED -> "Unmatched acceleration, blitz initiation, and sensory reaction latency."
            BattleCategory.DURABILITY -> "Impenetrable physiological resilience and endurance that outlasts offensive barrages."
            BattleCategory.POWER -> "Catastrophic energy output and versatility of exotic powers."
            BattleCategory.COMBAT -> "Supreme martial fluency, counter-striking mastery, and tactical combat reflexes."
        }

        val loserDeficit = comparisons.filter { it.winnerSlot == winnerSlot }.maxByOrNull { abs(it.delta) }
        val keyVulnerability = "${loser.name}'s deficit in ${loserDeficit?.category?.displayName ?: "stat endurance"} (+${abs(loserDeficit?.delta ?: 0)} disadvantage) leaves them exposed to decisive neutralization."

        val summary = buildString {
            append("${winner.name} secures a ${winProb}% outcome probability under ${scenario.title}. ")
            if (tierDiff != 0) {
                append("The tier separation places ${winner.name} on a distinctly higher operational magnitude. ")
            }
            if (majorLeads.isNotEmpty()) {
                val leadNames = majorLeads.take(2).joinToString(" and ") { it.category.displayName }
                append("Dominance in $leadNames allows ${winner.name} to control the combat tempo and dictate engagement conditions.")
            }
        }

        return Triple(decidingFactor, keyVulnerability, summary)
    }
}
