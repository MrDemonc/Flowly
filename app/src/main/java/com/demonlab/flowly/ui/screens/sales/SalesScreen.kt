package com.demonlab.flowly.ui.screens.sales

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.demonlab.flowly.data.local.dao.SaleWithRecipe
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
fun SalesScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: SalesViewModel = viewModel(factory = SalesViewModel.Factory(app.saleRepository))
) {
    val state by viewModel.state.collectAsState()
    var deleteTargetId by remember { mutableStateOf<Long?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Ventas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.sales.isEmpty() -> EmptyState(icon = Icons.Default.PointOfSale, title = "Sin ventas", description = "Registra tu primera venta")
                else -> LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    itemsIndexed(state.sales, key = { _, it -> it.id }) { index, sale ->
                        SaleCard(position = sectionPositionFromIndex(index, state.sales.size), sale = sale, onDelete = { deleteTargetId = sale.id })
                    }
                }
            }

            FloatingActionButton(
                onClick = { navController.navigate(Screen.SaleCreate.route) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva venta")
            }
        }
    }

    deleteTargetId?.let { id ->
        ConfirmDialog(title = "Eliminar venta", message = "Esta acción no se puede deshacer", onConfirm = { viewModel.deleteSale(id); deleteTargetId = null }, onDismiss = { deleteTargetId = null })
    }
}

@Composable
private fun SaleCard(sale: SaleWithRecipe, position: SectionPosition, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "PY")) }
    SectionCard(position = position) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sale.recipeName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text("${sale.quantity} x ${CurrencySymbol.current} ${formatNumber(sale.unitPrice)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(dateFormat.format(Date(sale.saleDate)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                Row {
                    Text("Total: ${CurrencySymbol.current} ${formatNumber(sale.totalAmount)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Text(" | Ganancia: ${CurrencySymbol.current} ${formatNumber(sale.profit)}", style = MaterialTheme.typography.bodySmall, color = if (sale.profit >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error)
                }
            }
            TextButton(onClick = onDelete) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
        }
    }
}

private fun formatNumber(value: Double): String = java.text.NumberFormat.getNumberInstance(Locale("es", "PY")).format(value)
