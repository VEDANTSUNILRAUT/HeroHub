package com.vedantraut.herohub.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions

@Composable
fun PopularHeroesSection(
    heroes: List<Hero>,
    onHeroClick: (Hero) -> Unit,
    modifier: Modifier = Modifier,
    favoriteHeroIds: Set<String> = emptySet(),
    onToggleFavorite: ((String) -> Unit)? = null
) {
    if (heroes.isEmpty()) return

    HeroSection(
        title = "🔥 Popular Heroes",
        subtitle = "Trending characters worldwide",
        badgeText = "${heroes.size}",
        modifier = modifier
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            items(heroes, key = { it.id }) { hero ->
                CompactHeroCard(
                    hero = hero,
                    onClick = { onHeroClick(hero) },
                    isFavorite = favoriteHeroIds.contains(hero.id),
                    onToggleFavorite = onToggleFavorite?.let { { it(hero.id) } }
                )
            }
        }
    }
}
