package com.vedantraut.herohub.domain.usecase

import com.vedantraut.herohub.domain.battle.BattleEngine
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.model.battle.BattleResult
import com.vedantraut.herohub.domain.model.battle.BattleScenario

class SimulateBattleUseCase(
    private val battleEngine: BattleEngine = BattleEngine()
) {
    operator fun invoke(
        fighter1: Hero,
        fighter2: Hero,
        scenario: BattleScenario = BattleScenario.STANDARD
    ): BattleResult {
        return battleEngine.evaluateBattle(
            fighter1 = fighter1,
            fighter2 = fighter2,
            scenario = scenario
        )
    }
}
