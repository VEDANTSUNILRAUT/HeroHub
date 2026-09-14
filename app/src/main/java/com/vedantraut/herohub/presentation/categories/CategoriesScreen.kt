package com.vedantraut.herohub.presentation.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.presentation.home.components.HeroCard
import com.vedantraut.herohub.presentation.home.components.HeroDetailBottomSheet
import com.vedantraut.herohub.presentation.home.components.HeroImage
import com.vedantraut.herohub.presentation.home.components.HomeEmptyView
import com.vedantraut.herohub.presentation.home.components.HomeErrorView
import com.vedantraut.herohub.presentation.home.components.HomeLoadingSkeleton
import com.vedantraut.herohub.presentation.home.components.RankedHeroCard
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = koinViewModel(),
    onNavigateToSearch: (String?) -> Unit = {}
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
                        errorMessage = state.error ?: "Failed to load categories",
                        onRetry = { viewModel.onIntent(CategoriesIntent.Retry) }
                    )
                }
                else -> {
                    if (state.selectedCategory == null) {
                        CategoriesOverviewContent(
                            state = state,
                            onIntent = viewModel::onIntent,
                            onNavigateToSearch = onNavigateToSearch
                        )
                    } else {
                        CategoryDrillDownContent(
                            state = state,
                            onIntent = viewModel::onIntent
                        )
                    }
                }
            }

            // Hero Detail Bottom Sheet
            state.selectedHeroForDetail?.let { hero ->
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onIntent(CategoriesIntent.DismissHeroDetail) },
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
                            onClose = { viewModel.onIntent(CategoriesIntent.DismissHeroDetail) },
                            modifier = Modifier.widthIn(max = 680.dp),
                            isFavorite = state.favoriteHeroIds.contains(hero.id),
                            onToggleFavorite = { viewModel.onIntent(CategoriesIntent.ToggleFavorite(hero.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriesOverviewContent(
    state: CategoriesState,
    onIntent: (CategoriesIntent) -> Unit,
    onNavigateToSearch: (String?) -> Unit
) {
    val configuration = LocalConfiguration.current
    val gridColumns = when {
        configuration.screenWidthDp >= 840 -> 3
        configuration.screenWidthDp >= 600 -> 2
        else -> 1
    }

    val filteredCategories = if (state.selectedGroup == null) {
        state.categories
    } else {
        state.categories.filter { it.group == state.selectedGroup }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = HeroHubDimensions.space32),
        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space20)
    ) {
        // 1. Header Banner & Quick Search Jump
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                    .padding(top = HeroHubDimensions.space8),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Explore Universes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Browse by publisher, factions, powers & species",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(48.dp)
                ) {
                    IconButton(
                        onClick = { onNavigateToSearch(null) },
                        modifier = Modifier.semantics { contentDescription = "Open search screen" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 2. Recently Viewed / Frequently Explored Heroes
        if (state.recentlyViewedHeroes.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                ) {
                    Text(
                        text = "Recently Explored",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
                    ) {
                        items(state.recentlyViewedHeroes) { hero ->
                            RecentlyViewedHeroItem(
                                hero = hero,
                                onClick = { onIntent(CategoriesIntent.SelectHero(hero)) }
                            )
                        }
                    }
                }
            }
        }

        // 3. Category Group Selector Pills
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
            ) {
                item {
                    val isSelected = state.selectedGroup == null
                    FilterChip(
                        selected = isSelected,
                        onClick = { onIntent(CategoriesIntent.SelectGroup(null)) },
                        label = { Text("All Categories", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        shape = RoundedCornerShape(HeroHubRadius.full),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                items(CategoryGroup.entries) { group ->
                    val isSelected = state.selectedGroup == group
                    FilterChip(
                        selected = isSelected,
                        onClick = { onIntent(CategoriesIntent.SelectGroup(group)) },
                        label = { Text(group.displayName, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        shape = RoundedCornerShape(HeroHubRadius.full),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        // 4. Category Cards Grid
        val chunkedCategories = filteredCategories.chunked(gridColumns)
        items(
            items = chunkedCategories,
            key = { row -> row.joinToString("-") { it.id } }
        ) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)
            ) {
                for (category in rowItems) {
                    Box(modifier = Modifier.weight(1f)) {
                        CategoryVisualCard(
                            category = category,
                            onClick = { onIntent(CategoriesIntent.SelectCategory(category)) }
                        )
                    }
                }
                if (rowItems.size < gridColumns) {
                    repeat(gridColumns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(HeroHubDimensions.space16))
        }
    }
}

@Composable
private fun RecentlyViewedHeroItem(
    hero: Hero,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(HeroHubRadius.large))
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Quick access to ${hero.name}" },
        shape = RoundedCornerShape(HeroHubRadius.large),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(HeroHubDimensions.space8),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
        ) {
            HeroImage(
                imageUrl = hero.imageUrl,
                contentDescription = hero.name,
                modifier = Modifier.size(40.dp),
                shape = CircleShape
            )
            Column {
                Text(
                    text = hero.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "⚡ ${hero.powerRating}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun CategoryVisualCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HeroHubRadius.extraLarge))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "${category.title}, ${category.estimatedHeroCount} characters, ${category.description}"
            },
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                HeroImage(
                    imageUrl = category.bannerImageUrl,
                    contentDescription = category.title,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(
                        topStart = HeroHubRadius.extraLarge,
                        topEnd = HeroHubRadius.extraLarge
                    )
                )

                // Dark gradient scrim for contrast and accessibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.9f)
                                )
                            )
                        )
                )

                // Category Count Badge
                Surface(
                    shape = RoundedCornerShape(bottomStart = HeroHubRadius.medium),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "${category.estimatedHeroCount}+ Heroes",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = HeroHubDimensions.space8, vertical = HeroHubDimensions.space4)
                    )
                }

                // Category title over scrim
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(HeroHubDimensions.space12)
                ) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HeroHubDimensions.space12),
                verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
            ) {
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Subcategory preview pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space4)
                ) {
                    items(category.subcategories.take(3)) { sub ->
                        Surface(
                            shape = RoundedCornerShape(HeroHubRadius.small),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = sub.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (category.subcategories.size > 3) {
                        item {
                            Text(
                                text = "+${category.subcategories.size - 3} more",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDrillDownContent(
    state: CategoriesState,
    onIntent: (CategoriesIntent) -> Unit
) {
    val category = state.selectedCategory ?: return
    val configuration = LocalConfiguration.current
    val gridColumns = when {
        configuration.screenWidthDp >= 840 -> 4
        configuration.screenWidthDp >= 600 -> 3
        else -> 2
    }

    var sortMenuExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(category.id, state.selectedSubcategory?.id, state.searchFilterText) {
        listState.scrollToItem(0)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = HeroHubDimensions.space32),
        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)
    ) {
        // 1. Drill-down Top App Bar with back action and sorting
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                    .padding(top = HeroHubDimensions.space8),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = { onIntent(CategoriesIntent.SelectCategory(null)) },
                        modifier = Modifier.semantics { contentDescription = "Return to all categories" }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Column {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val countLabel = if (state.categoryHeroes.size < state.totalCategoryHeroesCount) {
                            "Showing ${state.categoryHeroes.size} of ${state.totalCategoryHeroesCount} characters"
                        } else {
                            "${state.totalCategoryHeroesCount} characters found"
                        }
                        Text(
                            text = countLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Controls: View Mode & Sort Order
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // View Mode Switcher
                    IconButton(
                        onClick = {
                            val nextMode = if (state.viewMode == CategoryViewMode.GRID) CategoryViewMode.LIST else CategoryViewMode.GRID
                            onIntent(CategoriesIntent.SetViewMode(nextMode))
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Switch to ${if (state.viewMode == CategoryViewMode.GRID) "List" else "Grid"} view"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Toggle view mode",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Sort Order Menu Trigger
                    Box {
                        IconButton(
                            onClick = { sortMenuExpanded = true },
                            modifier = Modifier.semantics { contentDescription = "Change sort order" }
                        ) {
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
                            CategorySortOrder.entries.forEach { sortOrder ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = sortOrder.displayName,
                                            fontWeight = if (sortOrder == state.sortOrder) FontWeight.Bold else FontWeight.Normal,
                                            color = if (sortOrder == state.sortOrder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        onIntent(CategoriesIntent.SetSortOrder(sortOrder))
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Subcategory Pills Filter Row
        if (category.subcategories.isNotEmpty()) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                ) {
                    item {
                        val isAllSelected = state.selectedSubcategory == null
                        FilterChip(
                            selected = isAllSelected,
                            onClick = { onIntent(CategoriesIntent.SelectSubcategory(null)) },
                            label = { Text("All ${category.title}") },
                            shape = RoundedCornerShape(HeroHubRadius.full),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }

                    items(category.subcategories) { subcategory ->
                        val isSelected = state.selectedSubcategory == subcategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { onIntent(CategoriesIntent.SelectSubcategory(subcategory)) },
                            label = { Text(subcategory.title) },
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

        // 3. In-Category Quick Filter Text Field
        item {
            OutlinedTextField(
                value = state.searchFilterText,
                onValueChange = { onIntent(CategoriesIntent.SearchWithinCategory(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
                placeholder = {
                    Text(
                        text = "Filter within ${category.title}...",
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
                        visible = state.searchFilterText.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = { onIntent(CategoriesIntent.SearchWithinCategory("")) }) {
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
        }

        // 4. Character Display: Grid or List
        if (state.categoryHeroes.isEmpty()) {
            item {
                HomeEmptyView(
                    query = state.searchFilterText.ifBlank { category.title },
                    category = state.selectedSubcategory?.title ?: category.title
                )
            }
        } else {
            if (state.viewMode == CategoryViewMode.GRID) {
                val chunked = state.categoryHeroes.chunked(gridColumns)
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
                                    onClick = { onIntent(CategoriesIntent.SelectHero(hero)) },
                                    isFavorite = state.favoriteHeroIds.contains(hero.id),
                                    onToggleFavorite = { onIntent(CategoriesIntent.ToggleFavorite(hero.id)) }
                                )
                            }
                        }
                        if (rowItems.size < gridColumns) {
                            repeat(gridColumns - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(HeroHubDimensions.space12))
                }
            } else {
                // List Mode
                items(
                    items = state.categoryHeroes,
                    key = { it.id }
                ) { hero ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                    ) {
                        RankedHeroCard(
                            rank = hero.powerRating,
                            hero = hero,
                            onClick = { onIntent(CategoriesIntent.SelectHero(hero)) },
                            isFavorite = state.favoriteHeroIds.contains(hero.id),
                            onToggleFavorite = { onIntent(CategoriesIntent.ToggleFavorite(hero.id)) }
                        )
                    }
                    Spacer(modifier = Modifier.height(HeroHubDimensions.space12))
                }
            }

            // Pagination Footer: Automatically loads next batch when user scrolls to bottom
            if (state.hasMoreHeroes) {
                item(key = "pagination_loading_footer") {
                    LaunchedEffect(state.categoryHeroes.size) {
                        onIntent(CategoriesIntent.LoadMoreHeroes)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = HeroHubDimensions.space16),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Loading next batch (${state.categoryHeroes.size} of ${state.totalCategoryHeroesCount})...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else if (state.categoryHeroes.isNotEmpty() && state.totalCategoryHeroesCount > 36) {
                item(key = "pagination_completed_footer") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = HeroHubDimensions.space16),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓ All ${state.totalCategoryHeroesCount} heroes loaded",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}