package com.demonlab.flowly.ui.screens.dashboard

import com.demonlab.flowly.core.util.CurrencySymbol
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    app: FlowlyApp,
    viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(
            app.saleRepository,
            app.expenseRepository,
            app.purchaseRepository,
            app.productionRepository
        )
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
            title = {
                Text(
                    text = "Flowly",
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
        
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Hoy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Ingresos",
                            value = formatCurrency(state.todayRevenue),
                            modifier = Modifier.weight(1f),
                            valueColor = MaterialTheme.colorScheme.primary
                        )
                        StatCard(
                            title = "Ganancia",
                            value = formatCurrency(state.todayProfit),
                            modifier = Modifier.weight(1f),
                            valueColor = if (state.todayProfit >= 0)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Ventas",
                            value = "${state.todaySalesCount}",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Gastos",
                            value = formatCurrency(state.todayExpenses),
                            modifier = Modifier.weight(1f),
                            valueColor = MaterialTheme.colorScheme.error
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Este Mes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Ingresos",
                            value = formatCurrency(state.monthRevenue),
                            modifier = Modifier.weight(1f),
                            valueColor = MaterialTheme.colorScheme.primary
                        )
                        StatCard(
                            title = "Ganancia",
                            value = formatCurrency(state.monthProfit),
                            modifier = Modifier.weight(1f),
                            valueColor = if (state.todayProfit >= 0)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Gastos",
                            value = formatCurrency(state.monthExpenses),
                            modifier = Modifier.weight(1f),
                            valueColor = MaterialTheme.colorScheme.error
                        )
                        StatCard(
                            title = "Compras",
                            value = formatCurrency(state.monthPurchases),
                            modifier = Modifier.weight(1f),
                            valueColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "PY"))
    return "${CurrencySymbol.current} ${formatter.format(amount)}"
}
