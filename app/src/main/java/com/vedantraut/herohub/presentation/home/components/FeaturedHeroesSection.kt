package com.vedantraut.herohub.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        configuration.screenWidthDp >= 600 -> 240.dp
        else -> 220.dp
    }

    val pagerState = rememberPagerState(pageCount = { heroes.size })

    HeroSection(
        title = "✨ Featured Spotlight",
        subtitle = "Multiverse icons & fan-favorite champions",
        badgeText = "${pagerState.currentPage + 1}/${heroes.size}",
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
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

            // Animated Capsule Dots Pager Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = HeroHubDimensions.space4),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(heroes.size) { iteration ->
                    val isCurrent = pagerState.currentPage == iteration
                    val width by animateDpAsState(
                        targetValue = if (isCurrent) 22.dp else 6.dp,
                        label = "indicatorWidth"
                    )
                    val color by animateColorAsState(
                        targetValue = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                        label = "indicatorColor"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedHeroCard(
    hero: Hero,
    onClick: () -> Unit
) {
    val publisherColor = when {
        hero.publisher.contains("Marvel", ignoreCase = true) -> Color(0xFFE23636)
        hero.publisher.contains("DC", ignoreCase = true) -> Color(0xFF0078F0)
        hero.publisher.contains("Dark Horse", ignoreCase = true) -> Color(0xFFB91C1C)
        else -> Color(0xFF8B5CF6)
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(HeroHubRadius.extraLarge))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HeroImage(
                imageUrl = hero.imageUrl,
                contentDescription = hero.name,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(HeroHubRadius.extraLarge)
            )

            // Cinematic Scrim Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.45f),
                                Color.Black.copy(alpha = 0.94f)
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
                // Top Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(HeroHubRadius.small),
                        color = publisherColor,
                        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(HeroHubRadius.small))
                    ) {
                        Text(
                            text = hero.publisher.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    HeroAlignmentTag(alignment = hero.displayAlignment)
                }

                // Bottom Hero Details & Stat Pills
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space4)
                ) {
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
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Stat Pills Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatMiniBadge(label = "STR", value = hero.strength, color = Color(0xFFEF4444))
                        StatMiniBadge(label = "INT", value = hero.intelligence, color = Color(0xFF3B82F6))
                        StatMiniBadge(label = "SPD", value = hero.speed, color = Color(0xFF10B981))
                        StatMiniBadge(label = "PWR", value = hero.power, color = Color(0xFFF59E0B))

                        Spacer(modifier = Modifier.weight(1f))

                        // Overall Power Badge
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${hero.powerRating}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMiniBadge(
    label: String,
    value: Int,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(HeroHubRadius.small),
        color = Color.Black.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = "$value",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
