package com.vedantraut.herohub.presentation.favorites

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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.TextButton
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
import com.vedantraut.herohub.presentation.home.components.HeroAlignmentTag
import com.vedantraut.herohub.presentation.home.components.HeroDetailBottomSheet
import com.vedantraut.herohub.presentation.home.components.HeroImage
import com.vedantraut.herohub.presentation.home.components.HomeLoadingSkeleton
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    onNavigateToExplore: () -> Unit = {}
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
                state.isLoading && state.favoriteHeroes.isEmpty() && state.recommendedHeroes.isEmpty() -> {
                    HomeLoadingSkeleton()
                }
                else -> {
                    FavoritesContent(
                        state = state,
                        onIntent = viewModel::onIntent,
                        onNavigateToExplore = onNavigateToExplore
                    )
                }
            }

            // Clear All Confirmation Dialog
            if (state.showClearConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.onIntent(FavoritesIntent.ShowClearDialog(false)) },
                    title = {
                        Text(
                            text = "Clear Squad?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = "This will remove all superheroes from your favorites roster.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.onIntent(FavoritesIntent.ConfirmClearAllFavorites) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Clear All")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.onIntent(FavoritesIntent.ShowClearDialog(false)) }) {
                            Text("Cancel")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(HeroHubRadius.extraLarge)
                )
            }

            // Hero Detail Bottom Sheet
            state.selectedHeroForDetail?.let { hero ->
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onIntent(FavoritesIntent.DismissHeroDetail) },
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
                            onClose = { viewModel.onIntent(FavoritesIntent.DismissHeroDetail) },
                            modifier = Modifier.widthIn(max = 680.dp),
                            isFavorite = state.favoriteHeroIds.contains(hero.id),
                            onToggleFavorite = { viewModel.onIntent(FavoritesIntent.ToggleFavorite(hero.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesContent(
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit,
    onNavigateToExplore: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val gridColumns = when {
        configuration.screenWidthDp >= 840 -> 4
        configuration.screenWidthDp >= 600 -> 3
        else -> 2
    }

    var sortMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = HeroHubDimensions.space32),
        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)
    ) {
        // 1. Squad Overview Battle Metrics Card
        if (state.favoriteHeroes.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                        .padding(top = HeroHubDimensions.space8),
                    shape = RoundedCornerShape(HeroHubRadius.extraLarge),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(HeroHubDimensions.space16),
                        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Superhero Squad",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Active assembled strike force",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            TextButton(
                                onClick = { onIntent(FavoritesIntent.ShowClearDialog(true)) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Clear All",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Stat 1: Total Heroes
                            SquadMetricItem(
                                label = "Total Squad",
                                value = "${state.favoriteHeroes.size}",
                                emoji = "🛡️"
                            )

                            // Stat 2: Avg Power
                            SquadMetricItem(
                                label = "Avg Power",
                                value = "${state.averagePower}%",
                                emoji = "⚡"
                            )

                            // Stat 3: Top Hero
                            SquadMetricItem(
                                label = "Top Champion",
                                value = state.topHero?.name ?: "N/A",
                                emoji = "👑"
                            )
                        }
                    }
                }
            }
        }

        // 2. Search & Filter Bar
        if (state.favoriteHeroes.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { onIntent(FavoritesIntent.SearchFavorites(it)) },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "Search squad...",
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
                                visible = state.searchQuery.isNotEmpty(),
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                IconButton(onClick = { onIntent(FavoritesIntent.SearchFavorites("")) }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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

                    // View Mode Switcher
                    IconButton(
                        onClick = {
                            val nextMode = if (state.viewMode == FavoritesViewMode.GRID) FavoritesViewMode.LIST else FavoritesViewMode.GRID
                            onIntent(FavoritesIntent.SetViewMode(nextMode))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Toggle view mode",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Sort Menu
                    Box {
                        IconButton(onClick = { sortMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Sort options",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            FavoritesSortOrder.entries.forEach { sort ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = sort.displayName,
                                            fontWeight = if (sort == state.sortOrder) FontWeight.Bold else FontWeight.Normal,
                                            color = if (sort == state.sortOrder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        onIntent(FavoritesIntent.SetSortOrder(sort))
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                ) {
                    listOf("All", "Marvel", "DC", "Indie").forEach { pub ->
                        item {
                            val isSelected = state.selectedPublisher == pub
                            FilterChip(
                                selected = isSelected,
                                onClick = { onIntent(FavoritesIntent.SelectPublisher(pub)) },
                                label = { Text(pub) },
                                shape = RoundedCornerShape(HeroHubRadius.full),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    listOf("Hero", "Villain").forEach { align ->
                        item {
                            val isSelected = state.selectedAlignment == align
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val newAlign = if (isSelected) "All" else align
                                    onIntent(FavoritesIntent.SelectAlignment(newAlign))
                                },
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
        }

        // 3. Favorites Results or Empty State
        if (state.isEmptySquad) {
            item {
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
                        Text(text = "⭐", fontSize = 48.sp)
                        Text(
                            text = "Your Superhero Squad is Empty",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Star your favorite heroes while browsing to build your dream superhero strike team and track collective squad stats.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

                        Button(
                            onClick = onNavigateToExplore,
                            shape = RoundedCornerShape(HeroHubRadius.large),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Explore Characters")
                        }
                    }
                }
            }

            // Recommended Heroes to Add
            if (state.recommendedHeroes.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                    ) {
                        Text(
                            text = "Recommended for Your Squad",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
                        ) {
                            items(state.recommendedHeroes) { hero ->
                                Card(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .clip(RoundedCornerShape(HeroHubRadius.large))
                                        .clickable { onIntent(FavoritesIntent.SelectHero(hero)) },
                                    shape = RoundedCornerShape(HeroHubRadius.large),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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

                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(6.dp)
                                            ) {
                                                IconButton(
                                                    onClick = { onIntent(FavoritesIntent.AddFavorite(hero.id)) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Text(text = "🤍", fontSize = 14.sp)
                                                }
                                            }
                                        }

                                        Column(modifier = Modifier.padding(HeroHubDimensions.space8)) {
                                            Text(
                                                text = hero.name,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "⚡ ${hero.powerRating}% power",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
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
        } else if (state.filteredHeroes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(HeroHubDimensions.space32),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No heroes match \"${state.searchQuery}\" in your squad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Roster Display: Grid or List
            if (state.viewMode == FavoritesViewMode.GRID) {
                item {
                    val chunked = state.filteredHeroes.chunked(gridColumns)
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
                                        FavoriteHeroGridCard(
                                            hero = hero,
                                            onClick = { onIntent(FavoritesIntent.SelectHero(hero)) },
                                            onRemove = { onIntent(FavoritesIntent.RemoveFavorite(hero.id)) }
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
                // List Mode
                items(state.filteredHeroes) { hero ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                    ) {
                        FavoriteHeroListCard(
                            hero = hero,
                            onClick = { onIntent(FavoritesIntent.SelectHero(hero)) },
                            onRemove = { onIntent(FavoritesIntent.RemoveFavorite(hero.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SquadMetricItem(
    label: String,
    value: String,
    emoji: String
) {
    Surface(
        shape = RoundedCornerShape(HeroHubRadius.medium),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(HeroHubDimensions.space8),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FavoriteHeroGridCard(
    hero: Hero,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HeroHubRadius.extraLarge))
            .clickable(onClick = onClick),
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

                // Remove heart button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(HeroHubDimensions.space8)
                ) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(text = "❤️", fontSize = 16.sp)
                    }
                }

                // Power Tag
                Surface(
                    shape = RoundedCornerShape(topStart = HeroHubRadius.small, bottomEnd = HeroHubRadius.medium),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = "⚡${hero.powerRating}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = HeroHubDimensions.space8, vertical = HeroHubDimensions.space4)
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
            }
        }
    }
}

@Composable
private fun FavoriteHeroListCard(
    hero: Hero,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HeroHubRadius.large))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(HeroHubRadius.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HeroHubDimensions.space12),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            HeroImage(
                imageUrl = hero.imageUrl,
                contentDescription = hero.name,
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(HeroHubRadius.medium)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hero.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = hero.publisher,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Text(
                text = "⚡ ${hero.powerRating}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = onRemove) {
                Text(text = "❤️", fontSize = 18.sp)
            }
        }
    }
}