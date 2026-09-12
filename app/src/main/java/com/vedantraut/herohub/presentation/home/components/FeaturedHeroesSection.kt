package com.vedantraut.herohub.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius

@Composable
fun FeaturedHeroesSection(
    heroes: List<Hero>,
    onHeroClick: (Hero) -> Unit,
    modifier: Modifier = Modifier
) {
    if (heroes.isEmpty()) return

    val configuration = LocalConfiguration.current
    val pagerHeight = when {
        configuration.screenWidthDp >= 840 -> 260.dp
        configuration.screenWidthDp >= 600 -> 230.dp
        else -> 210.dp
    }

    val pagerState = rememberPagerState(pageCount = { heroes.size })

    HeroSection(
        title = "✨ Featured Heroes",
        subtitle = "Spotlight superheroes & legends",
        badgeText = "${pagerState.currentPage + 1}/${heroes.size}",
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
            pageSpacing = HeroHubDimensions.space12,
            modifier = Modifier
                .fillMaxWidth()
                .height(pagerHeight)
        ) { page ->
            val hero = heroes[page]
            FeaturedHeroCard(
                hero = hero,
                onClick = { onHeroClick(hero) }
            )
        }
    }
}

@Composable
private fun FeaturedHeroCard(
    hero: Hero,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(HeroHubRadius.extraLarge))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HeroImage(
                imageUrl = hero.imageUrl,
                contentDescription = hero.name,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(HeroHubRadius.extraLarge)
            )

            // Scrim Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.92f)
                            )
                        )
                    )
            )

            // Content Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(HeroHubDimensions.cardPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PublisherTag(publisher = hero.publisher)
                    HeroAlignmentTag(alignment = hero.displayAlignment)
                }

                // Bottom Hero Details
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = hero.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (hero.realName.isNotBlank() && hero.realName != hero.name) {
                        Text(
                            text = hero.realName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Power Rating",
                                tint = Color.White,
                                modifier = Modifier.padding(end = HeroHubDimensions.space4)
                            )
                            Text(
                                text = "Power ${hero.powerRating}/100",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(HeroHubRadius.medium),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Inspect",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = HeroHubDimensions.space8, vertical = HeroHubDimensions.space4)
                            )
                        }
                    }
                }
            }
        }
    }
}
