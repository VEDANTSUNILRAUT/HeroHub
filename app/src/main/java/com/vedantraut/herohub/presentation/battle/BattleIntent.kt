package com.vedantraut.herohub.presentation.battle

import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.model.battle.BattleScenario

sealed interface BattleIntent {
    data class SelectFighter(val slot: Int, val hero: Hero) : BattleIntent
    data class SetScenario(val scenario: BattleScenario) : BattleIntent
    data object SwapFighters : BattleIntent
    data class OpenHeroPicker(val slot: Int) : BattleIntent
    data object DismissHeroPicker : BattleIntent
    data class UpdatePickerQuery(val query: String) : BattleIntent
    data object RandomizeMatchup : BattleIntent
    data class PreselectHero(val heroId: String) : BattleIntent
}
