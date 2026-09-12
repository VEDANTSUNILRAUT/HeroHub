package com.vedantraut.herohub.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions

@Composable
fun PowerRankingsSection(
    heroes: List<Hero>,
    onHeroClick: (Hero) -> Unit,
    modifier: Modifier = Modifier
) {
    if (heroes.isEmpty()) return

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

    HeroSection(
        title = "⚡ Power Rankings",
        subtitle = "Highest rated multiversal powerhouses",
        badgeText = "Top ${heroes.size}",
        modifier = modifier
    ) {
        if (isWideScreen) {
            val indexedHeroes = heroes.mapIndexed { index, hero -> index + 1 to hero }
            val chunked = indexedHeroes.chunked(2)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
            ) {
                for (row in chunked) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
                    ) {
                        for ((rank, hero) in row) {
                            Box(modifier = Modifier.weight(1f)) {
                                RankedHeroCard(
                                    rank = rank,
                                    hero = hero,
                                    onClick = { onHeroClick(hero) }
                                )
                            }
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
            ) {
                heroes.forEachIndexed { index, hero ->
                    RankedHeroCard(
                        rank = index + 1,
                        hero = hero,
                        onClick = { onHeroClick(hero) }
                    )
                }
            }
        }
    }
}
