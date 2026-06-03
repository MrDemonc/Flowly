package com.demonlab.flowly.ui.screens.reports

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.ui.components.StatCard
import com.demonlab.flowly.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: ReportsViewModel = viewModel(
        factory = ReportsViewModel.Factory(app.saleRepository, app.expenseRepository, app.purchaseRepository, app.productionRepository)
    )
) {
    val state by viewModel.state.collectAsState()
    val collapseLimit = with(LocalDensity.current) { 88.dp.toPx() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(initialHeightOffset = -collapseLimit)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        LargeTopAppBar(
            title = { Text("Reportes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary) },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Resumen del Mes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(title = "Ingresos", value = "${CurrencySymbol.current} ${formatNumber(state.totalRevenue)}", modifier = Modifier.weight(1f), valueColor = MaterialTheme.colorScheme.primary)
                        StatCard(title = "Ganancia Bruta", value = "${CurrencySymbol.current} ${formatNumber(state.totalProfit)}", modifier = Modifier.weight(1f), valueColor = if (state.totalProfit >= 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error)
                    }
                }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(title = "Gastos", value = "${CurrencySymbol.current} ${formatNumber(state.totalExpenses)}", modifier = Modifier.weight(1f), valueColor = MaterialTheme.colorScheme.error)
                        StatCard(title = "Compras", value = "${CurrencySymbol.current} ${formatNumber(state.totalPurchases)}", modifier = Modifier.weight(1f), valueColor = MaterialTheme.colorScheme.secondary)
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ganancia Neta", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("${CurrencySymbol.current} ${formatNumber(state.netProfit)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Text("Ventas: ${state.salesCount} transacciones", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (state.expensesByCategory.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("Gastos por Categoría", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    items(state.expensesByCategory) { cat ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(1.dp)) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(cat.category, style = MaterialTheme.typography.bodyLarge)
                                Text("${CurrencySymbol.current} ${formatNumber(cat.total)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

private fun formatNumber(value: Double) = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "PY")).format(value)
