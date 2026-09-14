package com.vedantraut.herohub.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius

@Composable
fun HeroCard(
    hero: Hero,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.88f)
            ) {
                HeroImage(
                    imageUrl = hero.imageUrl,
                    contentDescription = hero.name,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = HeroHubRadius.extraLarge, topEnd = HeroHubRadius.extraLarge)
                )

                // Top Floating Alignment & Power / Favorite Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(HeroHubDimensions.space8),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeroAlignmentTag(alignment = hero.displayAlignment)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space4)
                    ) {
                        if (onToggleFavorite != null) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable(onClick = onToggleFavorite)
                                ) {
                                    Text(text = if (isFavorite) "❤️" else "🤍", fontSize = 13.sp)
                                }
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${hero.powerRating}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = HeroHubDimensions.space8, vertical = HeroHubDimensions.space2)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HeroHubDimensions.contentPadding)
            ) {
                Text(
                    text = hero.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = hero.realName.ifBlank { hero.publisher },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                ) {
                    LinearProgressIndicator(
                        progress = { (hero.powerRating / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "${hero.powerRating}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CompactHeroCard(
    hero: Hero,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(136.dp),
        shape = RoundedCornerShape(HeroHubRadius.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                HeroImage(
                    imageUrl = hero.imageUrl,
                    contentDescription = hero.name,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = HeroHubRadius.large, topEnd = HeroHubRadius.large)
                )

                if (onToggleFavorite != null) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .size(28.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(onClick = onToggleFavorite)
                        ) {
                            Text(text = if (isFavorite) "❤️" else "🤍", fontSize = 12.sp)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(bottomStart = HeroHubRadius.medium),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "⚡${hero.powerRating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = HeroHubDimensions.space8, vertical = HeroHubDimensions.space2)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HeroHubDimensions.space8)
            ) {
                Text(
                    text = hero.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = hero.publisher,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RankedHeroCard(
    rank: Int,
    hero: Hero,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {
    val medalColor = when (rank) {
        1 -> Color(0xFFF59E0B)
        2 -> Color(0xFF94A3B8)
        3 -> Color(0xFFD97706)
        else -> MaterialTheme.colorScheme.primary
    }

    val tierLabel = when {
        hero.powerRating >= 95 -> "GOD TIER"
        hero.powerRating >= 90 -> "COSMIC"
        hero.powerRating >= 85 -> "ALPHA"
        else -> "ELITE"
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (rank <= 3) 1.5.dp else 0.5.dp,
                color = if (rank <= 3) medalColor.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(HeroHubRadius.large)
            ),
        shape = RoundedCornerShape(HeroHubRadius.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = if (rank <= 3) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HeroHubDimensions.space12),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            // Rank Badge with Medal or Number
            Surface(
                shape = CircleShape,
                color = if (rank <= 3) medalColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = medalColor
                ),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = when (rank) {
                            1 -> "🥇"
                            2 -> "🥈"
                            3 -> "🥉"
                            else -> "#$rank"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = if (rank <= 3) 14.sp else 12.sp
                    )
                }
            }

            // Hero Avatar with subtle shape
            HeroImage(
                imageUrl = hero.imageUrl,
                contentDescription = hero.name,
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(HeroHubRadius.medium)
            )

            // Info & Power Meter
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = hero.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(HeroHubRadius.small),
                        color = medalColor.copy(alpha = 0.18f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, medalColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = tierLabel,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = medalColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Text(
                    text = hero.realName.ifBlank { hero.publisher },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                LinearProgressIndicator(
                    progress = { (hero.powerRating / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(4.dp)
                        .clip(CircleShape),
                    color = medalColor,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )
            }

            // Power Rating & Heart
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space4)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space4)
                ) {
                    Text(
                        text = "⚡${hero.powerRating}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = medalColor
                    )

                    if (onToggleFavorite != null) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(onClick = onToggleFavorite)
                            ) {
                                Text(text = if (isFavorite) "❤️" else "🤍", fontSize = 13.sp)
                            }
                        }
                    }
                }
                HeroAlignmentTag(alignment = hero.displayAlignment)
            }
        }
    }
}

@Composable
fun HeroAlignmentTag(
    alignment: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(HeroHubRadius.small),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Text(
            text = alignment,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = HeroHubDimensions.space8, vertical = HeroHubDimensions.space2)
        )
    }
}

@Composable
fun PublisherTag(
    publisher: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(HeroHubRadius.small),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Text(
            text = publisher,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = HeroHubDimensions.space8, vertical = HeroHubDimensions.space4),
            maxLines = 1
        )
    }
}
