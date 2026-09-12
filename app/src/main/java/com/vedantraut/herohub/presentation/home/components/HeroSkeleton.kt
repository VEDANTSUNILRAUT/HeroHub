package com.vedantraut.herohub.presentation.home.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius

@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = translateAnim, y = translateAnim)
    )
}

@Composable
fun HomeLoadingSkeleton(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = HeroHubDimensions.space16),
        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space24)
    ) {
        // Search bar skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                .height(56.dp)
                .clip(RoundedCornerShape(HeroHubRadius.extraLarge))
                .background(brush)
        )

        // Category chips skeleton
        LazyRow(
            contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
        ) {
            items(5) {
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(HeroHubRadius.full))
                        .background(brush)
                )
            }
        }

        // Featured spotlight skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                .height(200.dp)
                .clip(RoundedCornerShape(HeroHubRadius.extraLarge))
                .background(brush)
        )

        // Popular row skeleton
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                    .width(140.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(HeroHubRadius.small))
                    .background(brush)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = HeroHubDimensions.screenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
            ) {
                items(4) {
                    Box(
                        modifier = Modifier
                            .width(136.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(HeroHubRadius.large))
                            .background(brush)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeErrorView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(HeroHubDimensions.space24),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.height(HeroHubDimensions.space16))

        Text(
            text = "Unable to load HeroHub",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(HeroHubDimensions.space20))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(HeroHubRadius.medium)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(HeroHubDimensions.space8))
            Text("Try Again", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun HomeEmptyView(
    query: String,
    category: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = HeroHubDimensions.space40, horizontal = HeroHubDimensions.space24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🦸", fontSize = 28.sp)
            }
        }

        Spacer(modifier = Modifier.height(HeroHubDimensions.space16))

        Text(
            text = "No Heroes Found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(HeroHubDimensions.space4))

        Text(
            text = if (query.isNotBlank()) "No superhero matches \"$query\" in $category." else "No superhero records available.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
