package com.demonlab.flowly.ui.screens.production

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
                                onDelete = { deleteTargetId = production.id }
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
}

@Composable
private fun ProductionCard(
    production: ProductionWithRecipe,
    position: SectionPosition,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "PY")) }

    SectionCard(
        position = position,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(production.recipeName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text("${production.quantity} unidades", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(dateFormat.format(Date(production.productionDate)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                Text("Costo: ${CurrencySymbol.current} ${formatNumber(production.totalCost)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            TextButton(onClick = onDelete) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private fun formatNumber(value: Double): String {
    return java.text.NumberFormat.getNumberInstance(Locale("es", "PY")).format(value)
}
