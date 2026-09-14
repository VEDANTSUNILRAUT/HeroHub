package com.vedantraut.herohub.presentation.navigation

sealed class AppDestination(val route: String) {

    data object Splash : AppDestination("splash")

    data object Home : AppDestination("home")

    data object Categories : AppDestination("categories")

    data object Search : AppDestination("search")

    data object Favorites : AppDestination("favorites")

    data object Battle : AppDestination("battle")

    data object Settings : AppDestination("settings")
}