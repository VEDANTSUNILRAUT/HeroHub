package com.vedantraut.herohub.presentation.home.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius

data class UniversePortalItem(
    val id: String,
    val categoryFilter: String,
    val title: String,
    val badge: String,
    val characterCount: String,
    val gradientColors: List<Color>,
    val emoji: String
)

object UniversePortals {
    val items = listOf(
        UniversePortalItem(
            id = "marvel",
            categoryFilter = "Marvel",
            title = "Marvel",
            badge = "EARTH-616",
            characterCount = "280+ Heroes",
            gradientColors = listOf(Color(0xFFE23636), Color(0xFF660E0E)),
            emoji = "🦸"
        ),
        UniversePortalItem(
            id = "dc",
            categoryFilter = "DC Comics",
            title = "DC Multiverse",
            badge = "PRIME EARTH",
            characterCount = "220+ Heroes",
            gradientColors = listOf(Color(0xFF0078F0), Color(0xFF002255)),
            emoji = "🦇"
        ),
        UniversePortalItem(
            id = "cosmic",
            categoryFilter = "Cosmic",
            title = "Cosmic & Gods",
            badge = "GOD TIER",
            characterCount = "Power 90+",
            gradientColors = listOf(Color(0xFFF59E0B), Color(0xFF78350F)),
            emoji = "⚡"
        ),
        UniversePortalItem(
            id = "indie",
            categoryFilter = "Indie",
            title = "Indie Legends",
            badge = "INDEPENDENT",
            characterCount = "100+ Heroes",
            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF3B0764)),
            emoji = "🪐"
        ),
        UniversePortalItem(
            id = "villains",
            categoryFilter = "Villains",
            title = "Villains Vault",
            badge = "ANTAGONISTS",
            characterCount = "Chaos & Evil",
            gradientColors = listOf(Color(0xFF991B1B), Color(0xFF18181B)),
            emoji = "💀"
        )
    )
}

@Composable
fun UniversePortalsSection(
    selectedCategory: String,
    onUniverseSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🌌 Multiverse Portals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Quick access to comic realms & factions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            items(UniversePortals.items, key = { it.id }) { portal ->
                val isSelected = portal.categoryFilter.equals(selectedCategory, ignoreCase = true)
                UniversePortalCard(
                    portal = portal,
                    isSelected = isSelected,
                    onClick = {
                        val target = if (isSelected) "All" else portal.categoryFilter
                        onUniverseSelected(target)
                    }
                )
            }
        }
    }
}

@Composable
private fun UniversePortalCard(
    portal: UniversePortalItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        label = "portalBorderColor"
    )

    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(HeroHubRadius.large))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) borderColor else portal.gradientColors.first().copy(alpha = 0.4f),
                shape = RoundedCornerShape(HeroHubRadius.large)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(HeroHubRadius.large),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = if (isSelected) {
                            portal.gradientColors
                        } else {
                            listOf(
                                portal.gradientColors.first().copy(alpha = 0.75f),
                                portal.gradientColors.last().copy(alpha = 0.9f)
                            )
                        }
                    )
                )
                .padding(HeroHubDimensions.space12)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = portal.emoji,
                        fontSize = 18.sp
                    )

                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = portal.badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = portal.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = portal.characterCount,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
