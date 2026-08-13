package com.demonlab.flowly.ui.screens.lotes

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
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.demonlab.flowly.core.designsystem.ExpressiveShapes
import com.demonlab.flowly.core.util.CurrencyFormatter
import com.demonlab.flowly.data.local.entity.BatchProductEntity
import com.demonlab.flowly.ui.components.CircleBackButton
import com.demonlab.flowly.ui.components.CircleIconBox
import com.demonlab.flowly.ui.components.CircleIconButton
import com.demonlab.flowly.ui.components.CustomExpressiveCard
import com.demonlab.flowly.ui.components.RegisterSaleDialog
import com.demonlab.flowly.ui.components.TransferDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoteDetailScreen(
    batchId: Long,
    app: FlowlyApp,
    navController: NavController,
    viewModel: LoteDetailViewModel = viewModel(
        factory = LoteDetailViewModel.Factory(batchId, app.batchRepository, app.settingsDataStore)
    )
) {
    val state by viewModel.uiState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Local 1, 1: Local 2
    var productToTransfer by remember { mutableStateOf<BatchProductEntity?>(null) }
    var productToSell by remember { mutableStateOf<Pair<BatchProductEntity, String>?>(null) }

    val batch = state.batch
    val local1Name = batch?.local1Name ?: "Local 1"
    val local2Name = batch?.local2Name ?: "Local 2"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LargeTopAppBar(
            title = {
                Text(
                    text = "Lote ${batch?.date ?: ""}",
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
            // Batch Financial Summary Card (Requirement 4)
            item {
                CustomExpressiveCard(
                    position = CardPosition.SINGLE,
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "RESUMEN DEL LOTE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Esperado",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = CurrencyFormatter.format(state.totalExpected, state.currencySymbol),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column {
                            Text(
                                text = "Unidades Restantes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${state.remainingUnits} un.",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Recaudado",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = CurrencyFormatter.format(state.totalPaid, state.currencySymbol),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column {
                            Text(
                                text = "Por Cobrar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
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

            // Local Selection Tabs
            item {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = local1Name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = local2Name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            val currentLocalTag = if (selectedTab == 0) "LOCAL_1" else "LOCAL_2"
            val currentLocalName = if (selectedTab == 0) local1Name else local2Name

            if (state.products.isEmpty()) {
                item {
                    CustomExpressiveCard(position = CardPosition.SINGLE) {
                        Text(
                            text = "No hay productos registrados en este lote.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                itemsIndexed(state.products) { index, product ->
                    val pos = ExpressiveShapes.getPositionForIndex(index, state.products.size)
                    val currentQty = if (selectedTab == 0) product.local1CurrentQty else product.local2CurrentQty
                    val initialQty = if (selectedTab == 0) product.local1InitialQty else product.local2InitialQty

                    CustomExpressiveCard(position = pos) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircleIconBox(
                                    icon = Icons.Default.Store,
                                    contentDescription = product.productName,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    iconTint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.padding(6.dp))
                                Column {
                                    Text(
                                        text = product.productName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${CurrencyFormatter.format(product.productPrice, state.currencySymbol)} / unit",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Stock: $currentQty / $initialQty un.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (currentQty > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircleIconButton(
                                    icon = Icons.AutoMirrored.Filled.CompareArrows,
                                    contentDescription = "Transferir",
                                    onClick = { productToTransfer = product },
                                    circleSize = 36.dp,
                                    iconSize = 18.dp,
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    iconTint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                CircleIconButton(
                                    icon = Icons.Default.PointOfSale,
                                    contentDescription = "Vender",
                                    onClick = { productToSell = Pair(product, currentLocalTag) },
                                    circleSize = 36.dp,
                                    iconSize = 18.dp,
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    iconTint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    productToTransfer?.let { product ->
        TransferDialog(
            product = product,
            local1Name = local1Name,
            local2Name = local2Name,
            onDismiss = { productToTransfer = null },
            onConfirm = { fromLocal, qty ->
                viewModel.transferProducts(product.id, fromLocal, qty) { }
                productToTransfer = null
            }
        )
    }

    productToSell?.let { (product, localTag) ->
        val name = if (localTag == "LOCAL_1") local1Name else local2Name
        RegisterSaleDialog(
            product = product,
            local = localTag,
            localName = name,
            currencySymbol = state.currencySymbol,
            onDismiss = { productToSell = null },
            onConfirm = { qty, paid, pending ->
                viewModel.registerSale(product.id, localTag, qty, paid, pending) { }
                productToSell = null
            }
        )
    }
}
