package com.vedantraut.herohub.presentation.favorites

enum class FavoritesSortOrder(val displayName: String) {
    POWER_DESC("Highest Power"),
    POWER_ASC("Lowest Power"),
    NAME_ASC("Alphabetical (A–Z)"),
    NAME_DESC("Alphabetical (Z–A)")
}

enum class FavoritesViewMode {
    GRID,
    LIST
}
