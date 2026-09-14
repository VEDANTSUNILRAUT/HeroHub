package com.vedantraut.herohub.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun HeroHubRoot() {

    val navController = rememberNavController()
    val currentBackStackEntry =
        navController.currentBackStackEntryAsState()

    val currentRoute =
        currentBackStackEntry.value?.destination?.route

    val showBars = currentRoute in listOf(
        AppDestination.Home.route,
        AppDestination.Battle.route,
        AppDestination.Categories.route,
        AppDestination.Search.route,
        AppDestination.Favorites.route,
        AppDestination.Settings.route
    )

    Scaffold(
        topBar = {
            if (showBars) {
                HeroHubTopBar(
                    currentRoute = currentRoute,
                )
            }
        },
        bottomBar = {
            if (showBars) {
                HeroHubBottomBar(
                    navController = navController
                )
            }
        }
    ){ innerPadding ->

        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}