package com.vedantraut.herohub.ui.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,

    secondary = Gray300,
    onSecondary = Black,

    tertiary = Gray500,
    onTertiary = White,

    background = Black,
    onBackground = White,

    surface = Gray950,
    onSurface = White,

    surfaceVariant = Gray900,
    onSurfaceVariant = Gray400,

    outline = Gray700,
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = White,

    secondary = Gray700,
    onSecondary = White,

    tertiary = Gray500,
    onTertiary = White,

    background = White,
    onBackground = Black,

    surface = Gray50,
    onSurface = Black,

    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,

    outline = Gray300,
)

@Composable
fun HeroHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HeroHubTypography,
        content = content
    )
}