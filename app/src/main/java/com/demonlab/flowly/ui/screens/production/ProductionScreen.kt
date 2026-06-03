package com.demonlab.flowly.ui.screens.production

import com.demonlab.flowly.core.util.CurrencySymbol
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.data.local.dao.ProductionWithRecipe
import com.demonlab.flowly.navigation.Screen
import com.demonlab.flowly.ui.components.ConfirmDialog
import com.demonlab.flowly.ui.components.EmptyState
import com.demonlab.flowly.ui.components.SectionCard
import com.demonlab.flowly.ui.components.SectionPosition
import com.demonlab.flowly.ui.components.sectionPositionFromIndex
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: ProductionViewModel = viewModel(
        factory = ProductionViewModel.Factory(app.productionRepository)
    )
) {
    val state by viewModel.state.collectAsState()
    val collapseLimit = with(LocalDensity.current) { 88.dp.toPx() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(initialHeightOffset = -collapseLimit)
    )
    var deleteTargetId by remember { mutableStateOf<Long?>(null) }
    var addBatchTargetId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LargeTopAppBar(
            title = {
                Text(
                    "Producción",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        
        Box(modifier = Modifier.weight(1f).nestedScroll(scrollBehavior.nestedScrollConnection)) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.productions.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.PrecisionManufacturing,
                        title = "Sin producciones",
                        description = "Registra tu primera producción"
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 400.dp)
                    ) {
                        itemsIndexed(state.productions, key = { _, it -> it.id }) { index, production ->
                            ProductionCard(
                                position = sectionPositionFromIndex(index, state.productions.size),
                                production = production,
                                onDelete = { deleteTargetId = production.id },
                                onAddBatch = { addBatchTargetId = production.id },
                                onUpdateSold = { sold -> viewModel.updateSold(production.id, sold) }
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { navController.navigate(Screen.ProductionCreate.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva producción")
            }
        }
    }

    deleteTargetId?.let { id ->
        ConfirmDialog(
            title = "Eliminar producción",
            message = "Esta acción no se puede deshacer",
            onConfirm = {
                viewModel.deleteProduction(id)
                deleteTargetId = null
            },
            onDismiss = { deleteTargetId = null }
        )
    }
    addBatchTargetId?.let { id ->
        val prod = state.productions.find { it.id == id }
        if (prod != null) {
            AddBatchDialog(
                currentQuantity = prod.quantity,
                onConfirm = { newQ ->
                    viewModel.updateQuantity(id, newQ)
                    addBatchTargetId = null
                },
                onDismiss = { addBatchTargetId = null }
            )
        }
    }
}

@Composable
private fun ProductionCard(
    production: ProductionWithRecipe,
    position: SectionPosition,
    onDelete: () -> Unit,
    onAddBatch: () -> Unit,
    onUpdateSold: (Int) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "PY")) }
    val totalUnits = production.quantity * production.recipeServings
    val remaining = totalUnits - production.sold
    val pillShape = RoundedCornerShape(50)

    SectionCard(
        position = position,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(production.recipeName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Producido: ${production.quantity} ${if (production.quantity == 1) "Lote" else "Lotes"} (${totalUnits} unidades)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Restante: $remaining unidades",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (remaining > 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                    )
                    Text(
                        dateFormat.format(Date(production.productionDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        "Costo: ${CurrencySymbol.current} ${formatNumber(production.totalCost)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(onClick = onAddBatch) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar lote",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(onClick = onDelete) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        pillShape
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Vendido",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onUpdateSold(production.sold - 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Restar",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            "${production.sold}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { onUpdateSold(production.sold + 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Sumar",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatNumber(value: Double): String {
    return java.text.NumberFormat.getNumberInstance(Locale("es", "PY")).format(value)
}

@Composable
private fun AddBatchDialog(
    currentQuantity: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var quantity by remember { mutableStateOf(currentQuantity) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustar lote") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cantidad de lotes", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { if (quantity > 1) quantity-- }) {
                        Icon(Icons.Default.Remove, contentDescription = "Reducir")
                    }
                    Text(
                        "$quantity",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    IconButton(onClick = { quantity++ }) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(quantity) },
                enabled = quantity != currentQuantity
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
