package com.vedantraut.herohub.presentation.settings

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vedantraut.herohub.domain.repository.ThemeMode
import com.vedantraut.herohub.ui.designsystem.token.HeroHubDimensions
import com.vedantraut.herohub.ui.designsystem.token.HeroHubRadius
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = HeroHubDimensions.space32),
                verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)
            ) {
                // 1. App Header Identity Card
                item {
                    AppIdentityCard(appVersion = state.appVersion)
                }

                // 2. Appearance & Theme
                item {
                    SettingsSectionCard(title = "Appearance & Theming") {
                        Column(verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)) {
                            Text(
                                text = "App Theme",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                            ) {
                                listOf(
                                    ThemeMode.SYSTEM to "System",
                                    ThemeMode.DARK to "Dark",
                                    ThemeMode.LIGHT to "Light"
                                ).forEach { (mode, label) ->
                                    val isSelected = state.themeMode == mode
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.onIntent(SettingsIntent.SetThemeMode(mode)) },
                                        label = { Text(label) },
                                        shape = RoundedCornerShape(HeroHubRadius.full),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Network & Offline Preferences
                item {
                    SettingsSectionCard(title = "Network & Data") {
                        Column(verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)) {
                            SettingsSwitchRow(
                                title = "Force Offline Mode",
                                subtitle = "Explore cached local database without making remote network queries",
                                checked = state.isOfflineForced,
                                onCheckedChange = { viewModel.onIntent(SettingsIntent.ToggleOfflineForced(it)) }
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            SettingsSwitchRow(
                                title = "Data Saver Mode",
                                subtitle = "Reduces network bandwidth and optimizes image decoding",
                                checked = state.isDataSaver,
                                onCheckedChange = { viewModel.onIntent(SettingsIntent.ToggleDataSaver(it)) }
                            )
                        }
                    }
                }

                // 4. Storage & Cache
                item {
                    SettingsSectionCard(title = "Storage & Memory") {
                        Column(verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Coil Image Cache",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Current storage usage: ${state.cacheSize}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewModel.onIntent(SettingsIntent.ShowClearCacheDialog(true)) },
                                    shape = RoundedCornerShape(HeroHubRadius.large)
                                ) {
                                    Text("Clear Cache")
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Reset Preferences",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Restore default theme and filters",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.onIntent(SettingsIntent.ShowResetDialog(true)) }
                                ) {
                                    Text("Reset All", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                // 5. Tech Stack & Architecture
                item {
                    SettingsSectionCard(title = "Technical Architecture") {
                        Column(verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
                            Text(
                                text = "Engineered with modern production standards:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                            ) {
                                TechBadge("Kotlin 2.0")
                                TechBadge("Jetpack Compose")
                                TechBadge("Material 3")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                            ) {
                                TechBadge("Koin DI")
                                TechBadge("Clean Architecture")
                                TechBadge("Coil Caching")
                            }
                        }
                    }
                }

                // 6. About & Credits
                item {
                    SettingsSectionCard(title = "About & Legal") {
                        Column(verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space8)
                            ) {
                                Text(text = "🛡️", fontSize = 20.sp)
                                Text(
                                    text = "HeroHub • Built by Vedant Raut",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = "Disclaimer: Superhero data, character names, and images are copyright of Marvel Characters, Inc., DC Comics, and their respective creators. This app is built for demonstration and educational purposes.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Clear Cache Dialog
            if (state.showClearCacheConfirm) {
                AlertDialog(
                    onDismissRequest = { viewModel.onIntent(SettingsIntent.ShowClearCacheDialog(false)) },
                    title = { Text("Clear Image Cache?") },
                    text = { Text("This will delete locally cached superhero portraits. They will be downloaded again when requested.") },
                    confirmButton = {
                        Button(onClick = { viewModel.onIntent(SettingsIntent.ConfirmClearCache) }) {
                            Text("Clear")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.onIntent(SettingsIntent.ShowClearCacheDialog(false)) }) {
                            Text("Cancel")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(HeroHubRadius.extraLarge)
                )
            }

            // Reset Dialog
            if (state.showResetConfirm) {
                AlertDialog(
                    onDismissRequest = { viewModel.onIntent(SettingsIntent.ShowResetDialog(false)) },
                    title = { Text("Reset All Settings?") },
                    text = { Text("This will restore default theme settings, offline switches, and clear all cache.") },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.onIntent(SettingsIntent.ConfirmResetAll) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Reset")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.onIntent(SettingsIntent.ShowResetDialog(false)) }) {
                            Text("Cancel")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(HeroHubRadius.extraLarge)
                )
            }
        }
    }
}

@Composable
private fun AppIdentityCard(appVersion: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding)
            .padding(top = HeroHubDimensions.space8),
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HeroHubDimensions.space20),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HeroHubDimensions.space16)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🦸", fontSize = 30.sp)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "HeroHub",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "The Multiverse Superhero Companion",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(HeroHubDimensions.space4))
                Surface(
                    shape = RoundedCornerShape(HeroHubRadius.small),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = appVersion,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HeroHubDimensions.screenHorizontalPadding),
        shape = RoundedCornerShape(HeroHubRadius.extraLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HeroHubDimensions.space16),
            verticalArrangement = Arrangement.spacedBy(HeroHubDimensions.space12)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(HeroHubDimensions.space12))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun TechBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(HeroHubRadius.small),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}