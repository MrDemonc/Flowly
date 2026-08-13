package com.demonlab.flowly.ui.screens.ajustes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.demonlab.flowly.core.designsystem.CardPosition
import com.demonlab.flowly.ui.components.CircleBackButton
import com.demonlab.flowly.ui.components.CircleIconBox
import com.demonlab.flowly.ui.components.CustomExpressiveCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcercaDeScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LargeTopAppBar(
            title = {
                Text(
                    text = "Acerca de",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            navigationIcon = {
                CircleBackButton(onBack = { navController.popBackStack() })
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App Header Card (Single)
            item {
                CustomExpressiveCard(
                    position = CardPosition.SINGLE,
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircleIconBox(
                            icon = Icons.Default.Info,
                            contentDescription = "Flowly",
                            circleSize = 64.dp,
                            iconSize = 32.dp,
                            containerColor = MaterialTheme.colorScheme.surface,
                            iconTint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Flowly Expressive",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Versión 2.0.0 (Build 2026)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Desarrollado por Demon Lab",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Features Card (First)
            item {
                CustomExpressiveCard(position = CardPosition.FIRST) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleIconBox(
                            icon = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                        Spacer(modifier = Modifier.padding(6.dp))
                        Column {
                            Text(
                                text = "Material 3 Expressive",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Interfaz adaptativa con colores dinámicos, animaciones con rebote y diseño de tarjetas agrupadas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Multi-local Management (Middle)
            item {
                CustomExpressiveCard(position = CardPosition.MIDDLE) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleIconBox(
                            icon = Icons.Default.Layers,
                            contentDescription = null,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                        Spacer(modifier = Modifier.padding(6.dp))
                        Column {
                            Text(
                                text = "Gestión Multilocal de Lotes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Lotes por fecha con división en dos locales personalizables y transferencias de productos en tiempo real.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Offline Privacy & Security (End)
            item {
                CustomExpressiveCard(position = CardPosition.END) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleIconBox(
                            icon = Icons.Default.Security,
                            contentDescription = null,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        Spacer(modifier = Modifier.padding(6.dp))
                        Column {
                            Text(
                                text = "Privacidad y Almacenamiento Local",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tus datos financieros y productos se almacenan de forma totalmente privada y segura en tu dispositivo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
