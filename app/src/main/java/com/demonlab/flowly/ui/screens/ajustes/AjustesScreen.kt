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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.core.designsystem.CardPosition
import com.demonlab.flowly.navigation.Screen
import com.demonlab.flowly.ui.components.CircleIconBox
import com.demonlab.flowly.ui.components.CustomExpressiveCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: AjustesViewModel = viewModel(
        factory = AjustesViewModel.Factory(app.settingsDataStore)
    )
) {
    val state by viewModel.uiState.collectAsState()
    var currencyInput by remember(state.currencySymbol) { mutableStateOf(state.currencySymbol) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LargeTopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleIconBox(
                        icon = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(
                        text = "Ajustes",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
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
            // SECCIÓN A: PERSONALIZACIÓN
            item {
                Text(
                    text = "Personalización",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Dark Mode option (First)
            item {
                CustomExpressiveCard(position = CardPosition.FIRST) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircleIconBox(
                                icon = Icons.Default.Palette,
                                contentDescription = null,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                            Spacer(modifier = Modifier.padding(6.dp))
                            Column {
                                Text(
                                    text = "Modo Oscuro",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Selecciona la apariencia preferida",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            FilterChip(
                                selected = state.themeMode == "system",
                                onClick = { viewModel.setThemeMode("system") },
                                label = { Text("Sistema") },
                                leadingIcon = { Icon(Icons.Default.SettingsSystemDaydream, contentDescription = null) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = state.themeMode == "light",
                                onClick = { viewModel.setThemeMode("light") },
                                label = { Text("Claro") },
                                leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = state.themeMode == "dark",
                                onClick = { viewModel.setThemeMode("dark") },
                                label = { Text("Oscuro") },
                                leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Currency Symbol option (End)
            item {
                CustomExpressiveCard(position = CardPosition.END) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircleIconBox(
                                icon = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                            Spacer(modifier = Modifier.padding(6.dp))
                            Column {
                                Text(
                                    text = "Símbolo de la Moneda",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Usado para formatear los importes financieros",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = currencyInput,
                            onValueChange = {
                                currencyInput = it
                                viewModel.setCurrencySymbol(it)
                            },
                            label = { Text("Símbolo de Moneda (ej. $, Gs, €, MXN)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // SECCIÓN B: NOTIFICACIONES
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notificaciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                CustomExpressiveCard(position = CardPosition.SINGLE) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            CircleIconBox(
                                icon = Icons.Default.Notifications,
                                contentDescription = null,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                            Spacer(modifier = Modifier.padding(6.dp))
                            Column {
                                Text(
                                    text = "Recordatorio: Cuentas por Cobrar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Alertas preventivas de saldos pendientes en lotes activos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = state.notifyPendingAccounts,
                            onCheckedChange = { viewModel.setNotifyPendingAccounts(it) }
                        )
                    }
                }
            }

            // SECCIÓN C: ACERCA DE
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Información",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                CustomExpressiveCard(
                    position = CardPosition.SINGLE,
                    onClick = {
                        navController.navigate(Screen.AcercaDe.route)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircleIconBox(
                                icon = Icons.Default.Info,
                                contentDescription = null,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                iconTint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.padding(6.dp))
                            Column {
                                Text(
                                    text = "Acerca de la Aplicación",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Versión 2.0.0 Expressive • Creado por Demon Lab",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        CircleIconBox(
                            icon = Icons.Default.ChevronRight,
                            contentDescription = "Abrir",
                            circleSize = 36.dp,
                            iconSize = 18.dp,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    }
                }
            }
        }
    }
}
