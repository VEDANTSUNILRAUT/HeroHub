package com.vedantraut.herohub.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius

@Composable
fun HeroImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(HeroHubRadius.large)
) {
    val context = LocalContext.current

    val cleanUrl = remember(imageUrl) {
        imageUrl
            .replace("http://", "https://")
            .replace("cdn.jsdelivr.net", "fastly.jsdelivr.net")
            .replace("gcore.jsdelivr.net", "fastly.jsdelivr.net")
            .replace("testingcf.jsdelivr.net", "fastly.jsdelivr.net")
    }

    val initial = remember(contentDescription) {
        contentDescription?.firstOrNull()?.uppercaseChar()?.toString() ?: "H"
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Fast background letter monogram rendered with zero subcomposition
        Text(
            text = initial,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            fontSize = 28.sp
        )

        // AsyncImage: Direct zero-subcomposition rendering with hardware bitmap decoding
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(cleanUrl)
                .crossfade(250)
                .allowHardware(true)
                .memoryCacheKey(cleanUrl)
                .diskCacheKey(cleanUrl)
                .setHeader("User-Agent", "Mozilla/5.0 (Android; Mobile; rv:128.0) Gecko/128.0 Firefox/128.0")
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )
    }
}
