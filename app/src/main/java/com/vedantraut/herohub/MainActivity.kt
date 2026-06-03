package com.vedantraut.herohub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.vedantraut.herohub.presentation.navigation.AppNavGraph
import com.vedantraut.herohub.presentation.navigation.HeroHubRoot
import com.vedantraut.herohub.ui.designsystem.theme.HeroHubTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            HeroHubTheme {
                HeroHubRoot()
            }
        }
    }
}