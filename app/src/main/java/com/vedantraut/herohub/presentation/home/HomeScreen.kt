package com.vedantraut.herohub.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vedantraut.herohub.domain.model.Hero
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
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
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
                        onIntent = viewModel::onIntent
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
    onIntent: (HomeIntent) -> Unit
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
        // 1. Search Bar
        item {
            HeroSearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(HomeIntent.SearchHero(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
            )
        }

        // 2. Categories Filter Chips
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
                    } else {
                        val chunked = state.filteredHeroes.chunked(columns)
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
                            }
                        }
                    }
                }
            }
        } else {
            // Default Phase 6 Home Experience (All 4 Core Discovery Sections)

            // Section 1: Featured Heroes Spotlight
            item {
                FeaturedHeroesSection(
                    heroes = state.featuredHeroes,
                    onHeroClick = { onIntent(HomeIntent.SelectHero(it)) }
                )
            }

            // Section 2: Popular Heroes Row
            item {
                PopularHeroesSection(
                    heroes = state.popularHeroes,
                    onHeroClick = { onIntent(HomeIntent.SelectHero(it)) },
                    favoriteHeroIds = state.favoriteHeroIds,
                    onToggleFavorite = { onIntent(HomeIntent.ToggleFavorite(it)) }
                )
            }

            // Section 3: Power Rankings Section
            item {
                PowerRankingsSection(
                    heroes = state.powerRankedHeroes,
                    onHeroClick = { onIntent(HomeIntent.SelectHero(it)) },
                    favoriteHeroIds = state.favoriteHeroIds,
                    onToggleFavorite = { onIntent(HomeIntent.ToggleFavorite(it)) }
                )
            }

            // Section 4: Recently Added Heroes Section
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
private fun HeroSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            Text(
                text = "Search heroes, villains, publishers...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
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
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
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
                    borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )
        }
    }
}