package com.vedantraut.herohub.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius

@Composable
fun ClashOfTheDaySection(
    fighter1: Hero,
    fighter2: Hero,
    onHeroClick: (Hero) -> Unit,
    onLaunchBattle: () -> Unit,
    modifier: Modifier = Modifier
) {
    HeroSection(
        title = "⚔️ Clash of the Day",
        subtitle = "Featured multiversal showdown",
        badgeText = "LIVE SIMULATION",
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
                .clip(RoundedCornerShape(HeroHubRadius.extraLarge)),
            shape = RoundedCornerShape(HeroHubRadius.extraLarge),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E1B4B),
                                Color(0xFF0F172A),
                                Color(0xFF311042)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.6f),
                                Color(0xFFF43F5E).copy(alpha = 0.6f)
                            )
                        ),
                        shape = RoundedCornerShape(HeroHubRadius.extraLarge)
                    )
                    .padding(HeroHubDimensions.space16)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)
                ) {
                    // Two Fighters Faceoff Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Fighter 1
                        FighterPreview(
                            hero = fighter1,
                            accentColor = Color(0xFF38BDF8),
                            onClick = { onHeroClick(fighter1) },
                            modifier = Modifier.weight(1f)
                        )

                        // VS Center Badge
                        Box(
                            modifier = Modifier.padding(horizontal = HeroHubDimensions.space8),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.7f),
                                border = androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    Brush.linearGradient(listOf(Color(0xFFF43F5E), Color(0xFFF59E0B)))
                                ),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "VS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Fighter 2
                        FighterPreview(
                            hero = fighter2,
                            accentColor = Color(0xFFF43F5E),
                            onClick = { onHeroClick(fighter2) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Bottom Action: Launch Simulation CTA Button
                    Button(
                        onClick = onLaunchBattle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(HeroHubRadius.full),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Simulate Battle",
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Simulate Clash in Arena",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FighterPreview(
    hero: Hero,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(HeroHubRadius.large))
            .clickable(onClick = onClick)
            .padding(HeroHubDimensions.space4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
    ) {
        // Hero Avatar with glowing border
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .border(2.dp, accentColor, CircleShape)
        ) {
            HeroImage(
                imageUrl = hero.imageUrl,
                contentDescription = hero.name,
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape
            )
        }

        // Hero Name
        Text(
            text = hero.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        // Publisher & Power Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space4)
        ) {
            Surface(
                shape = RoundedCornerShape(HeroHubRadius.small),
                color = accentColor.copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.6f))
            ) {
                Text(
                    text = "⚡ ${hero.powerRating}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
