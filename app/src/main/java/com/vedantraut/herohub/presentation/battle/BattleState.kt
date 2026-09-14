package com.vedantraut.herohub.presentation.battle

import androidx.compose.runtime.Immutable
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.model.battle.BattleResult
import com.vedantraut.herohub.domain.model.battle.BattleScenario

@Immutable
data class BattleState(
    val fighter1: Hero? = null,
    val fighter2: Hero? = null,
    val scenario: BattleScenario = BattleScenario.STANDARD,
    val battleResult: BattleResult? = null,
    val allHeroes: List<Hero> = emptyList(),
    val isLoading: Boolean = false,
    val isHeroPickerOpen: Boolean = false,
    val pickingSlot: Int = 1, // 1 for Fighter 1, 2 for Fighter 2
    val pickerSearchQuery: String = "",
    val error: String? = null
) {
    val filteredPickerHeroes: List<Hero>
        get() {
            if (pickerSearchQuery.isBlank()) return allHeroes
            return allHeroes.filter {
                it.name.contains(pickerSearchQuery, ignoreCase = true) ||
                        it.realName.contains(pickerSearchQuery, ignoreCase = true) ||
                        it.publisher.contains(pickerSearchQuery, ignoreCase = true)
            }
        }
}
