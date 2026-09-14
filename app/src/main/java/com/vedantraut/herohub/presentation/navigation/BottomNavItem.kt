package com.vedantraut.herohub.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Home",
        route = AppDestination.Home.route,
        icon = Icons.Outlined.Home
    ),
    BottomNavItem(
        title = "Versus",
        route = AppDestination.Battle.route,
        icon = Icons.Default.Refresh
    ),
    BottomNavItem(
        title = "Categories",
        route = AppDestination.Categories.route,
        icon = Icons.Outlined.CheckCircle
    ),
    BottomNavItem(
        title = "Search",
        route = AppDestination.Search.route,
        icon = Icons.Outlined.Search
    ),
    BottomNavItem(
        title = "Favorites",
        route = AppDestination.Favorites.route,
        icon = Icons.Outlined.FavoriteBorder
    ),
    BottomNavItem(
        title = "Settings",
        route = AppDestination.Settings.route,
        icon = Icons.Outlined.Settings
    )
)