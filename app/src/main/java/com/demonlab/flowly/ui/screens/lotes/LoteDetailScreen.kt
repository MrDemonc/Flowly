package com.demonlab.flowly.ui.screens.lotes

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import com.demonlab.flowly.ui.components.RegisterFiadoDialog
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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Local 1, 1: Local 2
    var productToTransfer by remember { mutableStateOf<BatchProductEntity?>(null) }
    var productToSell by remember { mutableStateOf<Pair<BatchProductEntity, String>?>(null) }
    var productToCredit by remember { mutableStateOf<Pair<BatchProductEntity, String>?>(null) }

    val batch = state.batch
    val isMultiLocal = !batch?.local2Name.isNullOrBlank()
    val local1Name = batch?.local1Name ?: "Local 1"
    val local2Name = batch?.local2Name ?: "Local 2"

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Lote ${batch?.date ?: ""}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    CircleBackButton(
                        onBack = { navController.popBackStack() }
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ==================== COLLAGE RESUMEN FINANCIERO ====================
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    // Hero Card: Total Esperado
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TOTAL ESPERADO DEL LOTE",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.MonetizationOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyFormatter.format(state.totalExpected, state.currencySymbol),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Collage Row 1: Recaudado vs Por Cobrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CollageStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Recaudado",
                            value = CurrencyFormatter.format(state.totalPaid, state.currencySymbol),
                            subtitle = "Efectivo cobrado",
                            icon = Icons.Default.MonetizationOn,
                            iconTint = MaterialTheme.colorScheme.primary,
                            iconBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )

                        CollageStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Por Cobrar",
                            value = CurrencyFormatter.format(state.totalPending, state.currencySymbol),
                            subtitle = if (state.pendingFiadosCount > 0) "${state.pendingFiadosCount} créditos activos" else "Sin saldos pendientes",
                            icon = Icons.Default.PendingActions,
                            iconTint = if (state.totalPending > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            iconBg = if (state.totalPending > 0) MaterialTheme.colorScheme.error.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    }

                    // Collage Row 2: Stock Restante vs Total Vendido
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CollageStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Stock Restante",
                            value = "${state.remainingUnits} un.",
                            subtitle = "En inventario",
                            icon = Icons.Default.Inventory2,
                            iconTint = MaterialTheme.colorScheme.secondary,
                            iconBg = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                        )

                        CollageStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Unidades Vendidas",
                            value = "${state.totalSoldUnits} un.",
                            subtitle = "Total despachadas",
                            icon = Icons.Default.PointOfSale,
                            iconTint = MaterialTheme.colorScheme.tertiary,
                            iconBg = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
                        )
                    }
                }
            }

            // ==================== LOCAL SELECTION TABS (SOLO SI TIENE 2 LOCALES) ====================
            if (isMultiLocal) {
                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
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
            }

            val currentLocalTag = if (isMultiLocal && selectedTab == 1) "LOCAL_2" else "LOCAL_1"
            val currentLocalName = if (isMultiLocal && selectedTab == 1) local2Name else local1Name

            // ==================== PRODUCTOS DEL LOCAL ====================
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
                    val currentQty = if (currentLocalTag == "LOCAL_1") product.local1CurrentQty else product.local2CurrentQty
                    val initialQty = if (currentLocalTag == "LOCAL_1") product.local1InitialQty else product.local2InitialQty

                    CustomExpressiveCard(position = pos) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                CircleIconBox(
                                    icon = Icons.Default.Store,
                                    contentDescription = product.productName,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    iconTint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = product.productName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${CurrencyFormatter.format(product.productPrice, state.currencySymbol)} / un.",
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

                            // Botones de acción: Transferir (si 2 locales) | A Crédito | Vender Contado
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (isMultiLocal) {
                                    CircleIconButton(
                                        icon = Icons.AutoMirrored.Filled.CompareArrows,
                                        contentDescription = "Transferir",
                                        onClick = { productToTransfer = product },
                                        circleSize = 36.dp,
                                        iconSize = 18.dp,
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        iconTint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                CircleIconButton(
                                    icon = Icons.Default.Handshake,
                                    contentDescription = "Registrar Por Cobrar",
                                    onClick = { productToCredit = Pair(product, currentLocalTag) },
                                    circleSize = 36.dp,
                                    iconSize = 18.dp,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    iconTint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                CircleIconButton(
                                    icon = Icons.Default.PointOfSale,
                                    contentDescription = "Vender Contado",
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

            // ==================== SECCIÓN DE CUENTAS POR COBRAR ====================
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AssignmentInd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cuentas por Cobrar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (state.fiados.isNotEmpty()) {
                        Surface(
                            shape = CircleShape,
                            color = if (state.pendingFiadosCount > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "${state.fiados.count { it.isPaid }}/${state.fiados.size} cobrados",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (state.pendingFiadosCount > 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            if (state.fiados.isEmpty()) {
                item {
                    CustomExpressiveCard(position = CardPosition.SINGLE) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircleIconBox(
                                icon = Icons.Default.Handshake,
                                contentDescription = null,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Sin cuentas por cobrar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Usa el botón de apretón de manos al lado de Vender para registrar ventas por cobrar con el nombre del cliente.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                itemsIndexed(state.fiados) { index, fiado ->
                    val pos = ExpressiveShapes.getPositionForIndex(index, state.fiados.size)
                    val localTag = if (fiado.local == "LOCAL_1") local1Name else local2Name
                    val amount = if (fiado.isPaid) fiado.amountPaid else fiado.amountPending

                    CustomExpressiveCard(position = pos) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircleIconBox(
                                        icon = Icons.Default.AssignmentInd,
                                        contentDescription = null,
                                        circleSize = 36.dp,
                                        iconSize = 18.dp,
                                        containerColor = if (fiado.isPaid) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                                        iconTint = if (fiado.isPaid) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = fiado.customerName ?: "Cliente",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = if (fiado.isPaid) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (fiado.isPaid) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (fiado.isPaid) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Text(
                                        text = if (fiado.isPaid) "COBRADO" else "POR COBRAR",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (fiado.isPaid) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isMultiLocal) "${fiado.quantity}x ${fiado.productName} • $localTag" else "${fiado.quantity}x ${fiado.productName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = CurrencyFormatter.format(amount, state.currencySymbol),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    textDecoration = if (fiado.isPaid) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (fiado.isPaid) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (!fiado.isPaid) {
                                        FilledTonalButton(
                                            onClick = { viewModel.toggleFiadoPaid(fiado.id) },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tachar (Cobró)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { viewModel.toggleFiadoPaid(fiado.id) },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Desmarcar", style = MaterialTheme.typography.labelMedium)
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteSale(fiado.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==================== DIALOGS ====================
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
            onConfirm = { qty, paid ->
                viewModel.registerSale(product.id, localTag, qty, paid) { }
                productToSell = null
            }
        )
    }

    productToCredit?.let { (product, localTag) ->
        val name = if (localTag == "LOCAL_1") local1Name else local2Name
        RegisterFiadoDialog(
            product = product,
            local = localTag,
            localName = name,
            currencySymbol = state.currencySymbol,
            onDismiss = { productToCredit = null },
            onConfirm = { qty, customerName, totalAmount ->
                viewModel.registerFiado(product.id, localTag, qty, customerName, totalAmount) { }
                productToCredit = null
            }
        )
    }
}

@Composable
private fun CollageStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
