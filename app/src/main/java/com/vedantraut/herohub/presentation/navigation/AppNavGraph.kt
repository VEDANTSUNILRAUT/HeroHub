package com.vedantraut.herohub.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vedantraut.herohub.presentation.categories.CategoriesScreen
import com.vedantraut.herohub.presentation.favorites.FavoritesScreen
import com.vedantraut.herohub.presentation.home.HomeScreen
import com.vedantraut.herohub.presentation.settings.SettingsScreen
import com.vedantraut.herohub.presentation.search.SearchScreen

import com.vedantraut.herohub.presentation.splash.SplashScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash.route,
        modifier = modifier
    ){

        composable(AppDestination.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppDestination.Home.route) {
            HomeScreen()
        }

        composable(AppDestination.Categories.route) {
            CategoriesScreen()
        }

        composable(AppDestination.Search.route) {
            SearchScreen()
        }

        composable(AppDestination.Favorites.route) {
            FavoritesScreen()
        }

        composable(AppDestination.Settings.route) {
            SettingsScreen()
        }
    }
}