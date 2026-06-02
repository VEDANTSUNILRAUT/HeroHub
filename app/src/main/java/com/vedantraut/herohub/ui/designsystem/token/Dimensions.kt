package com.vedantraut.herohub.ui.designsystem.token

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimensions(

    // Spacing
    val space2: Dp = 2.dp,
    val space4: Dp = 4.dp,
    val space8: Dp = 8.dp,
    val space12: Dp = 12.dp,
    val space16: Dp = 16.dp,
    val space20: Dp = 20.dp,
    val space24: Dp = 24.dp,
    val space32: Dp = 32.dp,
    val space40: Dp = 40.dp,
    val space48: Dp = 48.dp,

    // Screen Padding
    val screenHorizontalPadding: Dp = 16.dp,
    val screenVerticalPadding: Dp = 16.dp,

    // Components
    val cardPadding: Dp = 16.dp,
    val contentPadding: Dp = 12.dp,

    // App Bar
    val topBarHeight: Dp = 56.dp,

    // Buttons
    val buttonHeight: Dp = 48.dp,

    // Inputs
    val textFieldHeight: Dp = 56.dp,

    // Dividers
    val dividerThickness: Dp = 1.dp,
)