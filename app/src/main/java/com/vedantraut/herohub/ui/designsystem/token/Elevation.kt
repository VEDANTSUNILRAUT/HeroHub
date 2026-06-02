package com.vedantraut.herohub.ui.designsystem.token

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Elevation(

    val none: Dp = 0.dp,

    val small: Dp = 2.dp,

    val medium: Dp = 4.dp,

    val large: Dp = 8.dp,

    val extraLarge: Dp = 12.dp,
)