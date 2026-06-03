package com.demonlab.flowly.presentation.screens.profits

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.ui.components.StatCard
import com.demonlab.flowly.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitsScreen(
    app: FlowlyApp,
    viewModel: ProfitsViewModel = viewModel(factory = ProfitsViewModel.Factory(app.saleRepository, app.expenseRepository, app.purchaseRepository))
) {
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Ganancias", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))

        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfitPeriod.entries.forEach { period ->
                FilterChip(
                    selected = state.period == period,
                    onClick = { viewModel.setPeriod(period) },
                    label = { Text(period.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer, selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(title = "Ingresos", value = "${CurrencySymbol.current} ${formatNumber(state.totalRevenue)}", modifier = Modifier.fillMaxWidth(), valueColor = MaterialTheme.colorScheme.primary)
                StatCard(title = "Costo de Ventas", value = "${CurrencySymbol.current} ${formatNumber(state.totalCost)}", modifier = Modifier.fillMaxWidth(), valueColor = MaterialTheme.colorScheme.secondary)

                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ganancia Bruta", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text("${CurrencySymbol.current} ${formatNumber(state.grossProfit)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }

                HorizontalDivider()
                Text("Deducciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                StatCard(title = "Gastos", value = "${CurrencySymbol.current} ${formatNumber(state.totalExpenses)}", modifier = Modifier.fillMaxWidth(), valueColor = MaterialTheme.colorScheme.error)
                StatCard(title = "Compras", value = "${CurrencySymbol.current} ${formatNumber(state.totalPurchases)}", modifier = Modifier.fillMaxWidth(), valueColor = MaterialTheme.colorScheme.error)

                HorizontalDivider()

                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ganancia Neta", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("${CurrencySymbol.current} ${formatNumber(state.netProfit)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = if (state.netProfit >= 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error)
                    }
                }

                Text("Ventas: ${state.salesCount} transacciones", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private fun formatNumber(value: Double) = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "PY")).format(value)
