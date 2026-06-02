package com.vedantraut.herohub

import androidx.compose.runtime.*
import com.vedantraut.herohub.presentation.home.HomeScreen
import com.vedantraut.herohub.presentation.splash.SplashScreen

@Composable
fun HeroHubAppContent() {

    var showSplash by remember {
        mutableStateOf(true)
    }

    if (showSplash) {

        SplashScreen(
            onSplashFinished = {
                showSplash = false
            }
        )

    } else {

        HomeScreen()
    }
}