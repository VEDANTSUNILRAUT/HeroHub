package com.vedantraut.herohub.ui.designsystem.token

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class IconSize(

    val extraSmall: Dp = 12.dp,

    val small: Dp = 16.dp,

    val medium: Dp = 24.dp,

    val large: Dp = 32.dp,

    val extraLarge: Dp = 48.dp,
)