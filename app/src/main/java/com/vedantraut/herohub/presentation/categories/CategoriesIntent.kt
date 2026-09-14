package com.vedantraut.herohub.presentation.categories

import com.vedantraut.herohub.domain.model.Hero

sealed interface CategoriesIntent {
    data object LoadCategories : CategoriesIntent
    data class SelectGroup(val group: CategoryGroup?) : CategoriesIntent
    data class SelectCategory(val category: CategoryItem?) : CategoriesIntent
    data class SelectSubcategory(val subcategory: SubcategoryItem?) : CategoriesIntent
    data class SetViewMode(val viewMode: CategoryViewMode) : CategoriesIntent
    data class SetSortOrder(val sortOrder: CategorySortOrder) : CategoriesIntent
    data class SearchWithinCategory(val query: String) : CategoriesIntent
    data class SelectHero(val hero: Hero) : CategoriesIntent
    data class ToggleFavorite(val heroId: String) : CategoriesIntent
    data object DismissHeroDetail : CategoriesIntent
    data object LoadMoreHeroes : CategoriesIntent
    data object Retry : CategoriesIntent
}
