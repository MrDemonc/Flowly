package com.demonlab.flowly.ui.screens.resumen

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.core.designsystem.CardPosition
import com.demonlab.flowly.core.designsystem.ExpressiveShapes
import com.demonlab.flowly.core.util.CurrencyFormatter
import com.demonlab.flowly.navigation.Screen
import com.demonlab.flowly.ui.components.CircleIconBox
import com.demonlab.flowly.ui.components.CustomExpressiveCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: ResumenViewModel = viewModel(
        factory = ResumenViewModel.Factory(app.batchRepository, app.settingsDataStore)
    )
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LargeTopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleIconBox(
                        icon = Icons.Default.Analytics,
                        contentDescription = "Resumen",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(
                        text = "Resumen General",
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main Financial Summary Card (Tarjeta Single)
            item {
                CustomExpressiveCard(
                    position = CardPosition.SINGLE,
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "TOTAL ESPERADO",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = CurrencyFormatter.format(state.totalExpected, state.currencySymbol),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircleIconBox(
                                    icon = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    circleSize = 32.dp,
                                    iconSize = 16.dp,
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                                Spacer(modifier = Modifier.padding(4.dp))
                                Text(
                                    text = "Recaudado",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = CurrencyFormatter.format(state.totalPaid, state.currencySymbol),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircleIconBox(
                                    icon = Icons.Default.PendingActions,
                                    contentDescription = null,
                                    circleSize = 32.dp,
                                    iconSize = 16.dp,
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                                Spacer(modifier = Modifier.padding(4.dp))
                                Text(
                                    text = "Por Cobrar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = CurrencyFormatter.format(state.totalPending, state.currencySymbol),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Lotes Activos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (state.activeBatches.isEmpty()) {
                item {
                    CustomExpressiveCard(position = CardPosition.SINGLE) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircleIconBox(
                                icon = Icons.Default.Layers,
                                contentDescription = null,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No hay lotes activos",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Ve a la pestaña Lotes para registrar uno nuevo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(state.activeBatches) { index, batch ->
                    val pos = ExpressiveShapes.getPositionForIndex(index, state.activeBatches.size)
                    CustomExpressiveCard(
                        position = pos,
                        onClick = {
                            navController.navigate(Screen.LoteDetail.createRoute(batch.id))
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircleIconBox(
                                    icon = Icons.Default.Layers,
                                    contentDescription = "Lote",
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    iconTint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.padding(6.dp))
                                Column {
                                    Text(
                                        text = "Lote del ${batch.date}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${batch.local1Name} • ${batch.local2Name}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            CircleIconBox(
                                icon = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Ver Lote",
                                circleSize = 36.dp,
                                iconSize = 18.dp,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
