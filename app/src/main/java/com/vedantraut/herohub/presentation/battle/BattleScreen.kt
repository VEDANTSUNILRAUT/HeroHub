package com.vedantraut.herohub.presentation.battle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.domain.model.battle.BattleResult
import com.vedantraut.herohub.domain.model.battle.BattleScenario
import com.vedantraut.herohub.domain.model.battle.CategoryComparison
import com.vedantraut.herohub.domain.model.battle.PowerTier
import com.vedantraut.herohub.presentation.home.components.HeroImage
import com.vedantraut.herohub.presentation.home.components.PublisherTag
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius
import org.koin.androidx.compose.koinViewModel

val Fighter1Color = Color(0xFF2979FF) // Electric Blue
val Fighter2Color = Color(0xFFFF3D00) // Blazing Crimson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(
    viewModel: BattleViewModel = koinViewModel(),
    onNavigateBack: (() -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading && state.fighter1 == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = HeroHubDimensions.space40)
    ) {
        // Top Action Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = HeroHubDimensions.screenHorizontalPadding,
                        vertical = HeroHubDimensions.space8
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "⚔️ Versus Arena",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Cross-Universe Superhero Clash Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(onClick = { viewModel.onIntent(BattleIntent.RandomizeMatchup) }) {
                            Text(text = "🎲", fontSize = 16.sp)
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(onClick = { viewModel.onIntent(BattleIntent.SwapFighters) }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Swap Fighters",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Fighters Clash Header
        item {
            FightersClashHeader(
                fighter1 = state.fighter1,
                fighter2 = state.fighter2,
                fighter1Tier = state.battleResult?.fighter1Tier,
                fighter2Tier = state.battleResult?.fighter2Tier,
                onSelectFighter1 = { viewModel.onIntent(BattleIntent.OpenHeroPicker(1)) },
                onSelectFighter2 = { viewModel.onIntent(BattleIntent.OpenHeroPicker(2)) }
            )
        }

        // Scenario Selector Chips
        item {
            ScenarioSelectorSection(
                currentScenario = state.scenario,
                onScenarioSelected = { viewModel.onIntent(BattleIntent.SetScenario(it)) }
            )
        }

        // Final Verdict & Outcome Card
        state.battleResult?.let { result ->
            item {
                BattleVerdictCard(result = result)
            }

            // Stat Comparisons Breakdown
            item {
                StatComparisonsSection(result = result)
            }
        }

        // Methodology & Canon Scaling Rules Accordion
        item {
            PowerscalingRulesCard()
        }
    }

    // Hero Picker Bottom Sheet
    if (state.isHeroPickerOpen) {
        HeroPickerBottomSheet(
            slot = state.pickingSlot,
            heroes = state.filteredPickerHeroes,
            searchQuery = state.pickerSearchQuery,
            onQueryChanged = { viewModel.onIntent(BattleIntent.UpdatePickerQuery(it)) },
            onHeroSelected = { hero ->
                viewModel.onIntent(BattleIntent.SelectFighter(state.pickingSlot, hero))
            },
            onDismiss = { viewModel.onIntent(BattleIntent.DismissHeroPicker) }
        )
    }
}

@Composable
private fun FightersClashHeader(
    fighter1: Hero?,
    fighter2: Hero?,
    fighter1Tier: PowerTier?,
    fighter2Tier: PowerTier?,
    onSelectFighter1: () -> Unit,
    onSelectFighter2: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding, vertical = HeroHubDimensions.space8),
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(HeroHubDimensions.space12)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fighter 1 Card
                FighterSlotCard(
                    hero = fighter1,
                    tier = fighter1Tier,
                    accentColor = Fighter1Color,
                    alignment = Alignment.Start,
                    modifier = Modifier.weight(1f),
                    onClick = onSelectFighter1
                )

                Spacer(modifier = Modifier.width(HeroHubDimensions.space8))

                // Fighter 2 Card
                FighterSlotCard(
                    hero = fighter2,
                    tier = fighter2Tier,
                    accentColor = Fighter2Color,
                    alignment = Alignment.End,
                    modifier = Modifier.weight(1f),
                    onClick = onSelectFighter2
                )
            }

            // Middle VS Emblem
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun FighterSlotCard(
    hero: Hero?,
    tier: PowerTier?,
    accentColor: Color,
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(HeroHubRadius.large))
            .clickable(onClick = onClick)
            .background(accentColor.copy(alpha = 0.08f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(HeroHubRadius.large))
            .padding(HeroHubDimensions.space12),
        horizontalAlignment = alignment
    ) {
        if (hero != null) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(HeroHubRadius.medium))
                    .border(2.dp, accentColor, RoundedCornerShape(HeroHubRadius.medium))
            ) {
                HeroImage(
                    imageUrl = hero.imageUrl,
                    contentDescription = hero.name,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

            Text(
                text = hero.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = hero.publisher,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(HeroHubDimensions.space4))

            if (tier != null) {
                Surface(
                    shape = RoundedCornerShape(HeroHubRadius.small),
                    color = Color(tier.badgeColorHex).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(tier.badgeColorHex))
                ) {
                    Text(
                        text = tier.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(tier.badgeColorHex),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(HeroHubDimensions.space4))

            Text(
                text = "Tap to swap",
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(HeroHubRadius.medium))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "➕", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

            Text(
                text = "Select Fighter",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
private fun ScenarioSelectorSection(
    currentScenario: BattleScenario,
    onScenarioSelected: (BattleScenario) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding, vertical = HeroHubDimensions.space8)
    ) {
        Text(
            text = "⚙️ Battle Scenario Modifiers",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(HeroHubDimensions.space4))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
        ) {
            BattleScenario.entries.forEach { scenario ->
                val isSelected = scenario == currentScenario
                FilterChip(
                    selected = isSelected,
                    onClick = { onScenarioSelected(scenario) },
                    label = {
                        Text(
                            text = scenario.shortLabel,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(HeroHubDimensions.space4))

        Surface(
            shape = RoundedCornerShape(HeroHubRadius.medium),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = currentScenario.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(HeroHubDimensions.space8)
            )
        }
    }
}

@Composable
private fun BattleVerdictCard(result: BattleResult) {
    val isF1Winner = result.winnerSlot == 1
    val isDraw = result.winnerSlot == null
    val winnerColor = if (isF1Winner) Fighter1Color else if (result.winnerSlot == 2) Fighter2Color else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding, vertical = HeroHubDimensions.space8),
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HeroHubDimensions.space16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🏆 PREDICTED OUTCOME",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = winnerColor
                    )
                    Text(
                        text = if (isDraw) "Deadlock Stalemate" else "${result.winnerHero?.name} Victory",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!isDraw) {
                    Surface(
                        shape = RoundedCornerShape(HeroHubRadius.full),
                        color = winnerColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, winnerColor)
                    ) {
                        Text(
                            text = "${result.winProbability}% Win Rate",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = winnerColor,
                            modifier = Modifier.padding(horizontal = HeroHubDimensions.space12, vertical = HeroHubDimensions.space4)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(HeroHubDimensions.space12))

            // Deciding Factor Callout
            Surface(
                shape = RoundedCornerShape(HeroHubRadius.medium),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(HeroHubDimensions.space12)) {
                    Text(
                        text = "🎯 Deciding Factor:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = result.decidingFactor,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

            // Tactical Summary
            Text(
                text = result.tacticalSummary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

            // Key Vulnerability
            Text(
                text = "⚠️ ${result.keyVulnerability}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun StatComparisonsSection(result: BattleResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding, vertical = HeroHubDimensions.space8)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📊 Category Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${result.fighter1.name} (${result.fighter1Wins}) - (${result.fighter2Wins}) ${result.fighter2.name}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

        result.comparisons.forEach { comparison ->
            StatTugOfWarRow(
                comparison = comparison,
                fighter1Name = result.fighter1.name,
                fighter2Name = result.fighter2.name
            )
            Spacer(modifier = Modifier.height(HeroHubDimensions.space8))
        }
    }
}

@Composable
private fun StatTugOfWarRow(
    comparison: CategoryComparison,
    fighter1Name: String,
    fighter2Name: String
) {
    val total = (comparison.fighter1Effective + comparison.fighter2Effective).coerceAtLeast(1)
    val f1Fraction = comparison.fighter1Effective.toFloat() / total

    val animatedFraction by animateFloatAsState(
        targetValue = f1Fraction,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "tugOfWar"
    )

    Surface(
        shape = RoundedCornerShape(HeroHubRadius.large),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(HeroHubDimensions.space12)) {
            // Category title and values
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = comparison.category.emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = comparison.category.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                ) {
                    Text(
                        text = "${comparison.fighter1Effective}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Fighter1Color
                    )
                    Text(
                        text = "vs",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${comparison.fighter2Effective}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Fighter2Color
                    )
                }
            }

            Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

            // Tug of War Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .height(8.dp)
                        .background(Fighter1Color)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(8.dp)
                        .background(Fighter2Color)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Advantage indicator & description
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comparison.qualitativeAnalysis,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (comparison.winnerSlot != null) {
                    val winnerName = if (comparison.winnerSlot == 1) fighter1Name else fighter2Name
                    val badgeColor = if (comparison.winnerSlot == 1) Fighter1Color else Fighter2Color
                    Surface(
                        shape = RoundedCornerShape(HeroHubRadius.small),
                        color = badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Edge: $winnerName",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Tie",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PowerscalingRulesCard() {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding, vertical = HeroHubDimensions.space8),
        shape = RoundedCornerShape(HeroHubRadius.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HeroHubDimensions.space16)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📜 Powerscaling Methodology & Rules", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Text(text = if (isExpanded) "▲ Hide" else "▼ Show", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = HeroHubDimensions.space12)) {
                    Text(
                        text = "1. Canon Hierarchy:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Primary continuity canon (Marvel Earth-616, DC Prime Earth) takes full precedent. One-off gag feats and single-issue writer outliers are normalized.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

                    Text(
                        text = "2. Cross-Universe Equalization:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Exotic power systems (Speed Force, Mystic Arts, Asgardian divine physiology, alien genetics) operate on neutral cosmological ground without innate immunity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

                    Text(
                        text = "3. Power Tier Hierarchy:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• Street Tier (0-44 avg): Batman, Captain America, Joker\n• City Tier (45-69 avg): Spider-Man, Wolverine\n• Planetary Tier (70-87 avg): Flash, Hulk, Iron Man\n• Cosmic Tier (88-100 avg): Superman, Thor, Thanos, Doctor Strange",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeroPickerBottomSheet(
    slot: Int,
    heroes: List<Hero>,
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onHeroSelected: (Hero) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeroHubDimensions.space16)
                .padding(bottom = HeroHubDimensions.space32)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Fighter $slot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChanged,
                placeholder = { Text("Search heroes by name or universe...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(HeroHubRadius.large),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(HeroHubDimensions.space12))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
            ) {
                items(heroes, key = { it.id }) { hero ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onHeroSelected(hero) },
                        shape = RoundedCornerShape(HeroHubRadius.medium),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(HeroHubDimensions.space8),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
                        ) {
                            HeroImage(
                                imageUrl = hero.imageUrl,
                                contentDescription = hero.name,
                                modifier = Modifier.size(50.dp),
                                shape = RoundedCornerShape(HeroHubRadius.medium)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = hero.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = hero.publisher,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = "⚡${hero.powerRating}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
