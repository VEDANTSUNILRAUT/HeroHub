package com.vedantraut.herohub.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.presentation.home.components.HeroCard
import com.vedantraut.herohub.presentation.home.components.HeroDetailBottomSheet
import com.vedantraut.herohub.presentation.home.components.HeroImage
import com.vedantraut.herohub.presentation.home.components.HomeLoadingSkeleton
import com.vedantraut.herohub.presentation.home.components.RankedHeroCard
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel()
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
            SearchScreenContent(
                state = state,
                onIntent = viewModel::onIntent
            )

            // Filter Modal Bottom Sheet
            if (state.isFilterSheetOpen) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onIntent(SearchIntent.CloseFilterSheet) },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = HeroHubRadius.extraLarge,
                        topEnd = HeroHubRadius.extraLarge
                    )
                ) {
                    SearchFilterBottomSheetContent(
                        filters = state.filters,
                        onApply = { viewModel.onIntent(SearchIntent.ApplyFilters(it)) },
                        onReset = { viewModel.onIntent(SearchIntent.ResetFilters) },
                        onClose = { viewModel.onIntent(SearchIntent.CloseFilterSheet) }
                    )
                }
            }

            // Voice Search Modal Simulation Dialog
            if (state.isVoiceSearchDialogShown) {
                VoiceSearchDialog(
                    onDismiss = { viewModel.onIntent(SearchIntent.ToggleVoiceSearchDialog(false)) },
                    onSpeechRecognized = { viewModel.onIntent(SearchIntent.SubmitVoiceResult(it)) }
                )
            }

            // Hero Detail Bottom Sheet
            state.selectedHeroForDetail?.let { hero ->
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onIntent(SearchIntent.DismissHeroDetail) },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = HeroHubRadius.extraLarge,
                        topEnd = HeroHubRadius.extraLarge
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        HeroDetailBottomSheet(
                            hero = hero,
                            onClose = { viewModel.onIntent(SearchIntent.DismissHeroDetail) },
                            modifier = Modifier.widthIn(max = 680.dp),
                            isFavorite = state.favoriteHeroIds.contains(hero.id),
                            onToggleFavorite = { viewModel.onIntent(SearchIntent.ToggleFavorite(hero.id)) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchScreenContent(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit
) {
    val configuration = LocalConfiguration.current
    val gridColumns = when {
        configuration.screenWidthDp >= 840 -> 4
        configuration.screenWidthDp >= 600 -> 3
        else -> 2
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = HeroHubDimensions.space32),
        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)
    ) {
        // 1. Offline Mode Banner
        if (state.isOffline) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = HeroHubDimensions.space16, vertical = HeroHubDimensions.space8),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Offline indicator",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Offline Mode: Searching cached superhero universe",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Persistent Search Header Bar
        item {
            SearchHeader(
                query = state.searchQuery,
                onQueryChange = { onIntent(SearchIntent.UpdateQuery(it)) },
                onClear = { onIntent(SearchIntent.ClearQuery) },
                onVoiceClick = { onIntent(SearchIntent.ToggleVoiceSearchDialog(true)) },
                filterActiveCount = state.filters.activeCount,
                onFilterClick = { onIntent(SearchIntent.OpenFilterSheet) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                    .padding(top = HeroHubDimensions.space8)
            )
        }

        // 3. Real-Time Autocomplete Suggestions (Visible while typing)
        if (state.searchQuery.isNotBlank() && state.autocompleteSuggestions.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                    shape = RoundedCornerShape(HeroHubRadius.large),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(vertical = HeroHubDimensions.space4)) {
                        state.autocompleteSuggestions.forEach { suggestion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onIntent(SearchIntent.SelectSuggestion(suggestion)) }
                                    .padding(horizontal = HeroHubDimensions.space16, vertical = HeroHubDimensions.space12),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Search for $suggestion",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Loading Skeletons
        if (state.isLoading) {
            item {
                HomeLoadingSkeleton()
            }
        } else if (!state.isQueryActive) {
            // 5. Zero-Query Discovery State

            // 5a. Recent Searches History
            if (state.recentSearches.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Searches",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            TextButton(
                                onClick = { onIntent(SearchIntent.ClearAllRecentSearches) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Clear All",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8),
                            verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                        ) {
                            state.recentSearches.forEach { query ->
                                Surface(
                                    shape = RoundedCornerShape(HeroHubRadius.full),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .clickable { onIntent(SearchIntent.ExecuteSearch(query)) }
                                            .padding(start = HeroHubDimensions.space12, end = HeroHubDimensions.space4, top = 4.dp, bottom = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = query,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(
                                            onClick = { onIntent(SearchIntent.DeleteRecentSearch(query)) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove $query from history",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5b. Trending Searches Pill Cloud
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                    verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                ) {
                    Text(
                        text = "🔥 Trending Superheroes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8),
                        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                    ) {
                        state.trendingSearches.forEach { heroName ->
                            Surface(
                                shape = RoundedCornerShape(HeroHubRadius.full),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.clickable { onIntent(SearchIntent.ExecuteSearch(heroName)) }
                            ) {
                                Text(
                                    text = heroName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = HeroHubDimensions.space12, vertical = HeroHubDimensions.space8)
                                )
                            }
                        }
                    }
                }
            }

            // 5c. Quick Categories Shortcuts
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                    verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                ) {
                    Text(
                        text = "Explore by Universe",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                    ) {
                        listOf("Marvel", "DC Comics", "Heroes", "Villains").forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(HeroHubRadius.large),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onIntent(SearchIntent.ExecuteSearch(tag)) }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = HeroHubDimensions.space12)
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // 6. Active Query / Search Results State

            // 6a. Results Bar: Count & View Switcher
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                    ) {
                        Text(
                            text = "${state.searchResults.size} Heroes Found",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (!state.filters.isDefault) {
                            Surface(
                                shape = RoundedCornerShape(HeroHubRadius.small),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Filtered",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // View Mode Switcher
                    IconButton(
                        onClick = {
                            val next = if (state.viewMode == SearchViewMode.GRID) SearchViewMode.LIST else SearchViewMode.GRID
                            onIntent(SearchIntent.SetViewMode(next))
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Switch to ${if (state.viewMode == SearchViewMode.GRID) "List" else "Grid"} view"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Toggle view mode",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 6b. Search Results: Grid or List or Empty State
            if (state.searchResults.isEmpty()) {
                item {
                    SearchEmptyResultsView(
                        query = state.searchQuery,
                        onReset = {
                            onIntent(SearchIntent.ClearQuery)
                            onIntent(SearchIntent.ResetFilters)
                        },
                        onTrendingSelect = { onIntent(SearchIntent.ExecuteSearch(it)) }
                    )
                }
            } else {
                if (state.viewMode == SearchViewMode.GRID) {
                    item {
                        val chunked = state.searchResults.chunked(gridColumns)
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
                                                onClick = { onIntent(SearchIntent.SelectHero(hero)) },
                                                isFavorite = state.favoriteHeroIds.contains(hero.id),
                                                onToggleFavorite = { onIntent(SearchIntent.ToggleFavorite(hero.id)) }
                                            )
                                        }
                                    }
                                    if (rowItems.size < gridColumns) {
                                        repeat(gridColumns - rowItems.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Ranked List Mode
                    items(state.searchResults) { hero ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                        ) {
                            RankedHeroCard(
                                rank = hero.powerRating,
                                hero = hero,
                                onClick = { onIntent(SearchIntent.SelectHero(hero)) },
                                isFavorite = state.favoriteHeroIds.contains(hero.id),
                                onToggleFavorite = { onIntent(SearchIntent.ToggleFavorite(hero.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onVoiceClick: () -> Unit,
    filterActiveCount: Int,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = onClear) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search query",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onVoiceClick,
                        modifier = Modifier.semantics { contentDescription = "Voice search" }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🎤",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(HeroHubRadius.full),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        // Filter Button with Badge
        BadgedBox(
            badge = {
                if (filterActiveCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(text = "$filterActiveCount")
                    }
                }
            }
        ) {
            Surface(
                shape = CircleShape,
                color = if (filterActiveCount > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (filterActiveCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(
                    onClick = onFilterClick,
                    modifier = Modifier.semantics { contentDescription = "Open advanced filters ($filterActiveCount active)" }
                ) {
                    Text(
                        text = "⚙️",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchEmptyResultsView(
    query: String,
    onReset: () -> Unit,
    onTrendingSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding, vertical = HeroHubDimensions.space16),
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HeroHubDimensions.space24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            Text(
                text = "🛡️",
                fontSize = 48.sp
            )

            Text(
                text = "No superheroes found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (query.isNotBlank()) "We couldn't find any matches for \"$query\". Check your spelling or loosen active filters." else "No characters match the selected filters.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(HeroHubDimensions.space4))

            Button(
                onClick = onReset,
                shape = RoundedCornerShape(HeroHubRadius.large),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(HeroHubDimensions.space8))
                Text("Reset Search & Filters")
            }

            Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

            Text(
                text = "Or try searching for:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8),
                verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
            ) {
                listOf("Spider-Man", "Batman", "Iron Man", "Thanos", "Wolverine").forEach { hero ->
                    Surface(
                        shape = RoundedCornerShape(HeroHubRadius.full),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.clickable { onTrendingSelect(hero) }
                    ) {
                        Text(
                            text = hero,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchFilterBottomSheetContent(
    filters: SearchFilterState,
    onApply: (SearchFilterState) -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit
) {
    var selectedPublisher by remember { mutableStateOf(filters.publisher) }
    var selectedAlignment by remember { mutableStateOf(filters.alignment) }
    var minPower by remember { mutableFloatStateOf(filters.minPower.toFloat()) }
    var maxPower by remember { mutableFloatStateOf(filters.maxPower.toFloat()) }
    var selectedGender by remember { mutableStateOf(filters.gender) }
    var selectedSortOrder by remember { mutableStateOf(filters.sortOrder) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.space20)
            .padding(bottom = HeroHubDimensions.space32),
        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter & Sort Superheroes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(onClick = {
                selectedPublisher = "All"
                selectedAlignment = "All"
                minPower = 0f
                maxPower = 100f
                selectedGender = "All"
                selectedSortOrder = SearchSortOrder.RELEVANCE
                onReset()
            }) {
                Text(
                    text = "Reset All",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 1. Publisher Filter
        Column(verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
            Text(
                text = "Universe & Publisher",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
                listOf("All", "Marvel", "DC", "Indie").forEach { pub ->
                    item {
                        FilterChip(
                            selected = selectedPublisher == pub,
                            onClick = { selectedPublisher = pub },
                            label = { Text(pub) },
                            shape = RoundedCornerShape(HeroHubRadius.full),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        // 2. Alignment Filter
        Column(verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
            Text(
                text = "Moral Alignment",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
                listOf("All", "Hero", "Villain", "Anti-Hero").forEach { align ->
                    item {
                        FilterChip(
                            selected = selectedAlignment == align,
                            onClick = { selectedAlignment = align },
                            label = { Text(align) },
                            shape = RoundedCornerShape(HeroHubRadius.full),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        // 3. Power Rating Range Slider
        Column(verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space4)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Power Rating Range",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${minPower.toInt()} – ${maxPower.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            RangeSlider(
                value = minPower..maxPower,
                onValueChange = { range ->
                    minPower = range.start
                    maxPower = range.endInclusive
                },
                valueRange = 0f..100f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        // 4. Sort Order
        Column(verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
            Text(
                text = "Sort Results By",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
                SearchSortOrder.entries.forEach { sort ->
                    item {
                        FilterChip(
                            selected = selectedSortOrder == sort,
                            onClick = { selectedSortOrder = sort },
                            label = { Text(sort.displayName) },
                            shape = RoundedCornerShape(HeroHubRadius.full),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(HeroHubRadius.large)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    onApply(
                        SearchFilterState(
                            publisher = selectedPublisher,
                            alignment = selectedAlignment,
                            minPower = minPower.toInt(),
                            maxPower = maxPower.toInt(),
                            gender = selectedGender,
                            sortOrder = selectedSortOrder
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(HeroHubRadius.large),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Apply Filters")
            }
        }
    }
}

@Composable
private fun VoiceSearchDialog(
    onDismiss: () -> Unit,
    onSpeechRecognized: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
            ) {
                Text(text = "🎙️", fontSize = 24.sp)
                Text(
                    text = "Listening for Heroes...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Speak superhero names, publishers, or teams (e.g., \"Batman\", \"Avengers\", \"Marvel Villains\").",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(72.dp)
                        .padding(HeroHubDimensions.space8)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🔊", fontSize = 28.sp)
                    }
                }

                Text(
                    text = "Quick simulated prompts:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
                    listOf("Spider-Man", "Batman", "Thanos").forEach { prompt ->
                        Surface(
                            shape = RoundedCornerShape(HeroHubRadius.full),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { onSpeechRecognized(prompt) }
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(HeroHubRadius.extraLarge)
    )
}