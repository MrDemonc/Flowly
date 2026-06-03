package com.demonlab.flowly.presentation.screens.statistics

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.ui.components.StatCard
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    app: FlowlyApp,
    viewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModel.Factory(app.saleRepository, app.expenseRepository))
) {
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Estadísticas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Resumen del Mes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(title = "Ventas", value = "${state.totalSales}", modifier = Modifier.weight(1f))
                    StatCard(title = "Promedio/Venta", value = "${CurrencySymbol.current} ${formatNumber(state.avgSaleValue)}", modifier = Modifier.weight(1f), valueColor = MaterialTheme.colorScheme.primary)
                }

                StatCard(title = "Ingresos Totales", value = "${CurrencySymbol.current} ${formatNumber(state.totalRevenue)}", modifier = Modifier.fillMaxWidth(), valueColor = MaterialTheme.colorScheme.primary)
                StatCard(title = "Ganancia Total", value = "${CurrencySymbol.current} ${formatNumber(state.totalProfit)}", modifier = Modifier.fillMaxWidth(), valueColor = if (state.totalProfit >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error)

                if (state.salesByPaymentMethod.isNotEmpty()) {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("Métodos de Pago", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    state.salesByPaymentMethod.forEach { method ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(method.paymentMethod, style = MaterialTheme.typography.bodyLarge)
                                Text("${CurrencySymbol.current} ${formatNumber(method.totalAmount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private fun formatNumber(value: Double) = java.text.NumberFormat.getNumberInstance(Locale("es", "PY")).format(value)
