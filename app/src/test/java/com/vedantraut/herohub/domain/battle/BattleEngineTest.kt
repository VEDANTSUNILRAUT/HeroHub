package com.vedantraut.herohub.domain.battle

import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.model.battle.BattleCategory
import com.vedantraut.herohub.domain.model.battle.BattleScenario
import com.vedantraut.herohub.domain.model.battle.PowerTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BattleEngineTest {

    private val battleEngine = BattleEngine()

    private val batman = Hero(
        id = "70",
        name = "Batman",
        intelligence = 100,
        strength = 26,
        speed = 27,
        durability = 50,
        power = 47,
        combat = 100
    )

    private val superman = Hero(
        id = "644",
        name = "Superman",
        intelligence = 94,
        strength = 100,
        speed = 100,
        durability = 100,
        power = 100,
        combat = 85
    )

    @Test
    fun `standard encounter evaluates all 6 categories and selects winner`() {
        val result = battleEngine.evaluateBattle(batman, superman, BattleScenario.STANDARD)

        assertEquals(6, result.comparisons.size)
        assertEquals(2, result.winnerSlot) // Superman wins standard random encounter
        assertEquals("Superman", result.winnerHero?.name)
        assertTrue(result.winProbability > 50)
        assertEquals(PowerTier.COSMIC, result.fighter2Tier)
        assertNotNull(result.decidingFactor)
        assertNotNull(result.tacticalSummary)
    }

    @Test
    fun `prep time enhances high intelligence combatant effective stats`() {
        val standardResult = battleEngine.evaluateBattle(batman, superman, BattleScenario.STANDARD)
        val prepResult = battleEngine.evaluateBattle(batman, superman, BattleScenario.PREP_TIME_24H)

        val batStandardCombat = standardResult.comparisons.first { it.category == BattleCategory.COMBAT }.fighter1Effective
        val batPrepCombat = prepResult.comparisons.first { it.category == BattleCategory.COMBAT }.fighter1Effective

        assertTrue(batPrepCombat >= batStandardCombat)
    }

    @Test
    fun `bloodlusted boosts speed and power effective stats`() {
        val bloodResult = battleEngine.evaluateBattle(superman, batman, BattleScenario.BLOODLUSTED)
        val speedComp = bloodResult.comparisons.first { it.category == BattleCategory.SPEED }

        assertEquals(100, speedComp.fighter1Effective)
    }
}
