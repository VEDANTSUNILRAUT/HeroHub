package com.vedantraut.herohub.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vedantraut.herohub.presentation.battle.BattleScreen
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
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(220)) + slideInHorizontally(
                animationSpec = tween(220),
                initialOffsetX = { 40 }
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(180)) + slideOutHorizontally(
                animationSpec = tween(180),
                targetOffsetX = { -40 }
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(220)) + slideInHorizontally(
                animationSpec = tween(220),
                initialOffsetX = { -40 }
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(180)) + slideOutHorizontally(
                animationSpec = tween(180),
                targetOffsetX = { 40 }
            )
        }
    ) {

        composable(
            route = AppDestination.Splash.route,
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
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
            HomeScreen(
                onNavigateToBattle = {
                    navController.navigate(AppDestination.Battle.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToCategories = {
                    navController.navigate(AppDestination.Categories.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppDestination.Battle.route) {
            BattleScreen()
        }

        composable(AppDestination.Categories.route) {
            CategoriesScreen(
                onNavigateToSearch = {
                    navController.navigate(AppDestination.Search.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppDestination.Search.route) {
            SearchScreen()
        }

        composable(AppDestination.Favorites.route) {
            FavoritesScreen(
                onNavigateToExplore = {
                    navController.navigate(AppDestination.Home.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppDestination.Settings.route) {
            SettingsScreen()
        }
    }
}