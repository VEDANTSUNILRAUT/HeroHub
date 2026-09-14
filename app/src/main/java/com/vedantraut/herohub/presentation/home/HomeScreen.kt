package com.vedantraut.herohub.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.presentation.home.components.ClashOfTheDaySection
import com.vedantraut.herohub.presentation.home.components.FeaturedHeroesSection
import com.vedantraut.herohub.presentation.home.components.HeroCard
import com.vedantraut.herohub.presentation.home.components.HeroDetailBottomSheet
import com.vedantraut.herohub.presentation.home.components.HeroSection
import com.vedantraut.herohub.presentation.home.components.HomeEmptyView
import com.vedantraut.herohub.presentation.home.components.HomeErrorView
import com.vedantraut.herohub.presentation.home.components.HomeLoadingSkeleton
import com.vedantraut.herohub.presentation.home.components.PopularHeroesSection
import com.vedantraut.herohub.presentation.home.components.PowerRankingsSection
import com.vedantraut.herohub.presentation.home.components.RecentlyAddedSection
import com.vedantraut.herohub.presentation.home.components.UniversePortalsSection
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToBattle: (() -> Unit)? = null,
    onNavigateToCategories: (() -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1200.dp)
        ) {
            when {
                state.isLoading && state.allHeroes.isEmpty() -> {
                    HomeLoadingSkeleton()
                }
                state.error != null && state.allHeroes.isEmpty() -> {
                    HomeErrorView(
                        errorMessage = state.error ?: "Unable to load superhero data",
                        onRetry = { viewModel.onIntent(HomeIntent.Retry) }
                    )
                }
                else -> {
                    HomeContent(
                        state = state,
                        onIntent = viewModel::onIntent,
                        onNavigateToBattle = onNavigateToBattle,
                        onNavigateToCategories = onNavigateToCategories
                    )
                }
            }

            // Hero Detail Modal Bottom Sheet
            state.selectedHeroForDetail?.let { hero ->
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onIntent(HomeIntent.DismissHeroDetail) },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = HeroHubRadius.extraLarge, topEnd = HeroHubRadius.extraLarge)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        HeroDetailBottomSheet(
                            hero = hero,
                            onClose = { viewModel.onIntent(HomeIntent.DismissHeroDetail) },
                            modifier = Modifier.widthIn(max = 680.dp),
                            isFavorite = state.favoriteHeroIds.contains(hero.id),
                            onToggleFavorite = { viewModel.onIntent(HomeIntent.ToggleFavorite(hero.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    onNavigateToBattle: (() -> Unit)?,
    onNavigateToCategories: (() -> Unit)?
) {
    val isSearching = state.searchQuery.isNotBlank()
    val isFiltered = state.selectedCategory != "All"
    val configuration = LocalConfiguration.current
    val columns = when {
        configuration.screenWidthDp >= 840 -> 4
        configuration.screenWidthDp >= 600 -> 3
        else -> 2
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = HeroHubDimensions.space32),
        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space20)
    ) {
        // 1. Multiverse Branding Header with Live Badge & Quick Battle shortcut
        item {
            HomeBrandedHeader(
                heroCount = state.allHeroes.size,
                onNavigateToBattle = onNavigateToBattle
            )
        }

        // 2. Glowing Modern Search Bar
        item {
            HeroSearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(HomeIntent.SearchHero(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
            )
        }

        // 3. Multiverse Universe Portals (Marvel, DC, Cosmic, Indie, Villains)
        item {
            UniversePortalsSection(
                selectedCategory = state.selectedCategory,
                onUniverseSelected = { onIntent(HomeIntent.SelectCategory(it)) }
            )
        }

        // 4. Categories Filter Chips Row with Icons
        item {
            CategoryChipsRow(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = { onIntent(HomeIntent.SelectCategory(it)) }
            )
        }

        // If searching or category filtering is active, show the filtered character grid
        if (isSearching || isFiltered) {
            item {
                HeroSection(
                    title = if (isSearching) "Search Results" else "${state.selectedCategory} Characters",
                    badgeText = "${state.filteredHeroes.size} found"
                ) {
                    if (state.filteredHeroes.isEmpty()) {
                        HomeEmptyView(
                            query = state.searchQuery,
                            category = state.selectedCategory
                        )
                    }
                }
            }

            if (state.filteredHeroes.isNotEmpty()) {
                val chunked = state.filteredHeroes.chunked(columns)
                items(
                    items = chunked,
                    key = { row -> row.joinToString("-") { it.id } }
                ) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
                    ) {
                        for (hero in rowItems) {
                            Box(modifier = Modifier.weight(1f)) {
                                HeroCard(
                                    hero = hero,
                                    onClick = { onIntent(HomeIntent.SelectHero(hero)) },
                                    isFavorite = state.favoriteHeroIds.contains(hero.id),
                                    onToggleFavorite = { onIntent(HomeIntent.ToggleFavorite(hero.id)) }
                                )
                            }
                        }
                        if (rowItems.size < columns) {
                            repeat(columns - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(HeroHubDimensions.space12))
                }
            }
        } else {
            // Default Multiverse Discovery Experience

            // Section 1: Cinematic Featured Heroes Spotlight
            item {
                FeaturedHeroesSection(
                    heroes = state.featuredHeroes,
                    onHeroClick = { onIntent(HomeIntent.SelectHero(it)) }
                )
            }

            // Section 2: Clash of the Day (Versus Showcase)
            state.clashHeroes?.let { (fighter1, fighter2) ->
                item {
                    ClashOfTheDaySection(
                        fighter1 = fighter1,
                        fighter2 = fighter2,
                        onHeroClick = { onIntent(HomeIntent.SelectHero(it)) },
                        onLaunchBattle = { onNavigateToBattle?.invoke() }
                    )
                }
            }

            // Section 3: Popular Heroes Row (Trending Worldwide)
            item {
                PopularHeroesSection(
                    heroes = state.popularHeroes,
                    onHeroClick = { onIntent(HomeIntent.SelectHero(it)) },
                    favoriteHeroIds = state.favoriteHeroIds,
                    onToggleFavorite = { onIntent(HomeIntent.ToggleFavorite(it)) }
                )
            }

            // Section 4: Power Rankings Leaderboard (🥇 Gold, 🥈 Silver, 🥉 Bronze)
            item {
                PowerRankingsSection(
                    heroes = state.powerRankedHeroes,
                    onHeroClick = { onIntent(HomeIntent.SelectHero(it)) },
                    favoriteHeroIds = state.favoriteHeroIds,
                    onToggleFavorite = { onIntent(HomeIntent.ToggleFavorite(it)) }
                )
            }

            // Section 5: Recently Added Multiverse Records
            item {
                RecentlyAddedSection(
                    heroes = state.recentlyAddedHeroes,
                    onHeroClick = { onIntent(HomeIntent.SelectHero(it)) },
                    favoriteHeroIds = state.favoriteHeroIds,
                    onToggleFavorite = { onIntent(HomeIntent.ToggleFavorite(it)) }
                )
            }
        }
    }
}

@Composable
private fun HomeBrandedHeader(
    heroCount: Int,
    onNavigateToBattle: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
            .padding(top = HeroHubDimensions.space4),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "HEROHUB",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = if (heroCount > 0) "$heroCount+ Multiverse Legends Archive" else "Multiverse Superheroes Archive",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (onNavigateToBattle != null) {
            Surface(
                shape = RoundedCornerShape(HeroHubRadius.full),
                color = Color(0xFF1E1B4B),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFFF43F5E)))
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(HeroHubRadius.full))
                    .clickable(onClick = onNavigateToBattle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "⚔️", fontSize = 12.sp)
                    Text(
                        text = "Versus",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(HeroHubRadius.full),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeroHubDimensions.space16, vertical = HeroHubDimensions.space12),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search heroes, villains, power levels...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChipsRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
    ) {
        items(categories) { category ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)
            val icon = when (category) {
                "All" -> "🌐"
                "Marvel" -> "🦸"
                "DC Comics" -> "🦇"
                "Indie" -> "🪐"
                "Cosmic" -> "⚡"
                "Heroes" -> "🛡️"
                "Villains" -> "💀"
                else -> "✨"
            }
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                leadingIcon = {
                    Text(text = icon, fontSize = 12.sp)
                },
                label = {
                    Text(
                        text = category,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                shape = RoundedCornerShape(HeroHubRadius.full),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
            )
        }
    }
}