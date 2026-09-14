package com.vedantraut.herohub.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vedantraut.herohub.domain.model.Hero
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroDetailBottomSheet(
    hero: Hero,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
    onNavigateToBattle: ((hero: Hero) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(horizontal = HeroHubDimensions.space20)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with portrait and titles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)
        ) {
            Box(modifier = Modifier.size(105.dp)) {
                HeroImage(
                    imageUrl = hero.imageUrl,
                    contentDescription = hero.name,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(HeroHubRadius.large)
                )

                if (onToggleFavorite != null) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(32.dp)
                    ) {
                        IconButton(onClick = onToggleFavorite) {
                            Text(text = if (isFavorite) "❤️" else "🤍", fontSize = 14.sp)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space4)
            ) {
                Text(
                    text = hero.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (hero.realName.isNotBlank()) {
                    Text(
                        text = hero.realName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(HeroHubDimensions.space4))

                Row(horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
                    PublisherTag(publisher = hero.publisher)
                    HeroAlignmentTag(alignment = hero.displayAlignment)
                }
            }
        }

        // Squad Favorite Button CTA
        if (onToggleFavorite != null) {
            Spacer(modifier = Modifier.height(HeroHubDimensions.space16))
            Button(
                onClick = onToggleFavorite,
                shape = RoundedCornerShape(HeroHubRadius.large),
                colors = if (isFavorite) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isFavorite) "❤️ In Superhero Squad (Favorite)" else "🤍 Add to Superhero Squad",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (onNavigateToBattle != null) {
            Spacer(modifier = Modifier.height(HeroHubDimensions.space8))
            Button(
                onClick = { onNavigateToBattle(hero) },
                shape = RoundedCornerShape(HeroHubRadius.large),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚔️ Challenge in Versus Arena",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(HeroHubDimensions.space20))

        // Power Statistics
        Text(
            text = "⚡ Power Statistics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(HeroHubDimensions.space8))

        PowerstatMetricRow(name = "Intelligence", value = hero.intelligence)
        PowerstatMetricRow(name = "Strength", value = hero.strength)
        PowerstatMetricRow(name = "Speed", value = hero.speed)
        PowerstatMetricRow(name = "Durability", value = hero.durability)
        PowerstatMetricRow(name = "Power", value = hero.power)
        PowerstatMetricRow(name = "Combat", value = hero.combat)

        Spacer(modifier = Modifier.height(HeroHubDimensions.space20))

        // Appearance
        Text(
            text = "👤 Physical Profile",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(HeroHubDimensions.space12))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8),
            verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
        ) {
            if (hero.gender.isNotBlank()) DetailChip(label = "Gender", value = hero.gender)
            if (hero.race.isNotBlank()) DetailChip(label = "Race", value = hero.race)
            if (hero.height.isNotBlank()) DetailChip(label = "Height", value = hero.height)
            if (hero.weight.isNotBlank()) DetailChip(label = "Weight", value = hero.weight)
        }

        Spacer(modifier = Modifier.height(HeroHubDimensions.space20))

        // Biography & Lore
        Text(
            text = "📖 Biography & Lore",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(HeroHubDimensions.space12))

        if (hero.firstAppearance.isNotBlank()) {
            BioRow(label = "First Appearance", value = hero.firstAppearance)
        }
        if (hero.placeOfBirth.isNotBlank()) {
            BioRow(label = "Place of Birth", value = hero.placeOfBirth)
        }
        if (hero.occupation.isNotBlank()) {
            BioRow(label = "Occupation", value = hero.occupation)
        }
        if (hero.groupAffiliation.isNotBlank()) {
            BioRow(label = "Affiliations", value = hero.groupAffiliation)
        }

        Spacer(modifier = Modifier.height(HeroHubDimensions.space24))
    }
}

@Composable
private fun PowerstatMetricRow(
    name: String,
    value: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HeroHubDimensions.space4)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$value/100",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(HeroHubDimensions.space4))

        LinearProgressIndicator(
            progress = { (value.coerceIn(0, 100)) / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun DetailChip(
    label: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(HeroHubRadius.medium),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = HeroHubDimensions.space8, vertical = HeroHubDimensions.space4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BioRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HeroHubDimensions.space4)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
