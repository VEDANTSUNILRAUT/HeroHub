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
fun RecentlyAddedSection(
    heroes: List<Hero>,
    onHeroClick: (Hero) -> Unit,
    modifier: Modifier = Modifier,
    favoriteHeroIds: Set<String> = emptySet(),
    onToggleFavorite: ((String) -> Unit)? = null
) {
    if (heroes.isEmpty()) return

    val configuration = LocalConfiguration.current
    val columns = when {
        configuration.screenWidthDp >= 840 -> 4
        configuration.screenWidthDp >= 600 -> 3
        else -> 2
    }

    HeroSection(
        title = "🆕 Recently Added",
        subtitle = "Newest character records in HeroHub",
        badgeText = "${heroes.size}",
        modifier = modifier
    ) {
        val chunked = heroes.chunked(columns)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            for (rowItems in chunked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
                ) {
                    for (hero in rowItems) {
                        Box(modifier = Modifier.weight(1f)) {
                            HeroCard(
                                hero = hero,
                                onClick = { onHeroClick(hero) },
                                isFavorite = favoriteHeroIds.contains(hero.id),
                                onToggleFavorite = onToggleFavorite?.let { { it(hero.id) } }
                            )
                        }
                    }
                    if (rowItems.size < columns) {
                        repeat(columns - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
