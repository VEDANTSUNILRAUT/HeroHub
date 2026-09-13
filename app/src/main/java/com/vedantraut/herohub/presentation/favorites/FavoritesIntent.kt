package com.vedantraut.herohub.presentation.favorites

import com.vedantraut.herohub.domain.model.Hero

sealed interface FavoritesIntent {
    data object LoadFavorites : FavoritesIntent
    data class ToggleFavorite(val heroId: String) : FavoritesIntent
    data class AddFavorite(val heroId: String) : FavoritesIntent
    data class RemoveFavorite(val heroId: String) : FavoritesIntent
    data class SearchFavorites(val query: String) : FavoritesIntent
    data class SelectPublisher(val publisher: String) : FavoritesIntent
    data class SelectAlignment(val alignment: String) : FavoritesIntent
    data class SetSortOrder(val sortOrder: FavoritesSortOrder) : FavoritesIntent
    data class SetViewMode(val viewMode: FavoritesViewMode) : FavoritesIntent
    data class SelectHero(val hero: Hero) : FavoritesIntent
    data object DismissHeroDetail : FavoritesIntent
    data class ShowClearDialog(val show: Boolean) : FavoritesIntent
    data object ConfirmClearAllFavorites : FavoritesIntent
}
