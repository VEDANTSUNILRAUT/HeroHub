package com.vedantraut.herohub.presentation.categories

import com.vedantraut.herohub.domain.model.Hero

data class CategoriesState(
    val isLoading: Boolean = true,
    val allHeroes: List<Hero> = emptyList(),
    val categories: List<CategoryItem> = CategoryPresets.defaultCategories,
    val selectedGroup: CategoryGroup? = null,
    val selectedCategory: CategoryItem? = null,
    val selectedSubcategory: SubcategoryItem? = null,
    val recentlyViewedHeroes: List<Hero> = emptyList(),
    val categoryHeroes: List<Hero> = emptyList(),
    val viewMode: CategoryViewMode = CategoryViewMode.GRID,
    val sortOrder: CategorySortOrder = CategorySortOrder.POWER_DESC,
    val selectedHeroForDetail: Hero? = null,
    val favoriteHeroIds: Set<String> = emptySet(),
    val error: String? = null,
    val searchFilterText: String = ""
)
