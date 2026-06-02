package com.vedantraut.herohub.ui.designsystem.token

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Radius(

    val none: Dp = 0.dp,

    val small: Dp = 4.dp,

    val medium: Dp = 8.dp,

    val large: Dp = 12.dp,

    val extraLarge: Dp = 16.dp,

    val full: Dp = 999.dp,
)