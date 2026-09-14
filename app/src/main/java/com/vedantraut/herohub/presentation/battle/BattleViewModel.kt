package com.vedantraut.herohub.presentation.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.usecase.GetHomeHeroesUseCase
import com.vedantraut.herohub.domain.usecase.SimulateBattleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BattleViewModel(
    private val getHomeHeroesUseCase: GetHomeHeroesUseCase,
    private val simulateBattleUseCase: SimulateBattleUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BattleState(isLoading = true))
    val state: StateFlow<BattleState> = _state.asStateFlow()

    init {
        loadHeroes()
    }

    fun onIntent(intent: BattleIntent) {
        when (intent) {
            is BattleIntent.SelectFighter -> handleSelectFighter(intent.slot, intent.hero)
            is BattleIntent.SetScenario -> handleSetScenario(intent.scenario)
            is BattleIntent.SwapFighters -> handleSwapFighters()
            is BattleIntent.OpenHeroPicker -> _state.update {
                it.copy(isHeroPickerOpen = true, pickingSlot = intent.slot, pickerSearchQuery = "")
            }
            is BattleIntent.DismissHeroPicker -> _state.update {
                it.copy(isHeroPickerOpen = false, pickerSearchQuery = "")
            }
            is BattleIntent.UpdatePickerQuery -> _state.update {
                it.copy(pickerSearchQuery = intent.query)
            }
            is BattleIntent.RandomizeMatchup -> handleRandomizeMatchup()
            is BattleIntent.PreselectHero -> handlePreselectHero(intent.heroId)
        }
    }

    private fun loadHeroes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val heroes = getHomeHeroesUseCase()
                val f1 = heroes.find { it.name.equals("Batman", ignoreCase = true) }
                    ?: heroes.firstOrNull()
                val f2 = heroes.find { it.name.equals("Iron Man", ignoreCase = true) }
                    ?: heroes.getOrNull(1)

                val result = if (f1 != null && f2 != null) {
                    simulateBattleUseCase(f1, f2, _state.value.scenario)
                } else null

                _state.update {
                    it.copy(
                        allHeroes = heroes,
                        fighter1 = f1,
                        fighter2 = f2,
                        battleResult = result,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load heroes"
                    )
                }
            }
        }
    }

    private fun handleSelectFighter(slot: Int, hero: Hero) {
        _state.update { current ->
            val newF1 = if (slot == 1) hero else current.fighter1
            val newF2 = if (slot == 2) hero else current.fighter2
            val result = if (newF1 != null && newF2 != null) {
                simulateBattleUseCase(newF1, newF2, current.scenario)
            } else current.battleResult

            current.copy(
                fighter1 = newF1,
                fighter2 = newF2,
                battleResult = result,
                isHeroPickerOpen = false,
                pickerSearchQuery = ""
            )
        }
    }

    private fun handleSetScenario(scenario: com.vedantraut.herohub.domain.model.battle.BattleScenario) {
        _state.update { current ->
            val result = if (current.fighter1 != null && current.fighter2 != null) {
                simulateBattleUseCase(current.fighter1, current.fighter2, scenario)
            } else current.battleResult

            current.copy(
                scenario = scenario,
                battleResult = result
            )
        }
    }

    private fun handleSwapFighters() {
        _state.update { current ->
            val newF1 = current.fighter2
            val newF2 = current.fighter1
            val result = if (newF1 != null && newF2 != null) {
                simulateBattleUseCase(newF1, newF2, current.scenario)
            } else null

            current.copy(
                fighter1 = newF1,
                fighter2 = newF2,
                battleResult = result
            )
        }
    }

    private fun handleRandomizeMatchup() {
        val heroes = _state.value.allHeroes
        if (heroes.size < 2) return

        val shuffled = heroes.shuffled()
        val f1 = shuffled[0]
        val f2 = shuffled[1]
        val result = simulateBattleUseCase(f1, f2, _state.value.scenario)

        _state.update {
            it.copy(
                fighter1 = f1,
                fighter2 = f2,
                battleResult = result
            )
        }
    }

    private fun handlePreselectHero(heroId: String) {
        val hero = _state.value.allHeroes.find { it.id == heroId } ?: return
        handleSelectFighter(1, hero)
    }
}
