package com.vedantraut.herohub.presentation.navigation

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroHubTopBar(
    currentRoute: String?
) {
    val title = when (currentRoute) {
        AppDestination.Home.route -> "HeroHub"
        AppDestination.Battle.route -> "Versus Arena"
        AppDestination.Categories.route -> "Categories"
        AppDestination.Search.route -> "Search"
        AppDestination.Favorites.route -> "Favorites"
        AppDestination.Settings.route -> "Settings"
        else -> "HeroHub"
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}