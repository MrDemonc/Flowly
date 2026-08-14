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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.core.designsystem.CardPosition
import com.demonlab.flowly.core.designsystem.ExpressiveShapes
import com.demonlab.flowly.core.util.CurrencyFormatter
import com.demonlab.flowly.ui.components.CircleBackButton
import com.demonlab.flowly.ui.components.CircleIconBox
import com.demonlab.flowly.ui.components.CustomExpressiveCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoteCreateScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: LoteCreateViewModel = viewModel(
        factory = LoteCreateViewModel.Factory(
            app.batchRepository,
            app.productRepository,
            app.settingsDataStore
        )
    )
) {
    val state by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val quantities = remember { mutableStateMapOf<Long, Pair<String, String>>() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Nuevo Lote",
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
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                state.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = {
                        quantities.forEach { (productId, pair) ->
                            val l1 = pair.first.toIntOrNull() ?: 0
                            val l2 = if (state.localCount == 1) 0 else (pair.second.toIntOrNull() ?: 0)
                            viewModel.setProductQuantity(productId, l1, l2)
                        }
                        viewModel.saveBatch { batchId ->
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Crear y Guardar Lote", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ==================== CONFIGURACIÓN DEL LOTE ====================
            item {
                CustomExpressiveCard(
                    position = CardPosition.SINGLE,
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Text(
                        text = "Configuración del Lote",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Selector de 1 o 2 Locales para este lote
                    Text(
                        text = "Cantidad de Locales:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.localCount == 1,
                            onClick = { viewModel.onLocalCountChange(1) },
                            label = { Text("1 Local Único") },
                            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = state.localCount == 2,
                            onClick = { viewModel.onLocalCountChange(2) },
                            label = { Text("2 Locales") },
                            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.date,
                        onValueChange = { viewModel.onDateChange(it) },
                        label = { Text("Fecha del Lote (dd/MM/yyyy)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.localCount == 1) {
                        OutlinedTextField(
                            value = state.local1Name,
                            onValueChange = { viewModel.onLocal1NameChange(it) },
                            label = { Text("Nombre del Local") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = state.local1Name,
                                onValueChange = { viewModel.onLocal1NameChange(it) },
                                label = { Text("Local 1 (Principal)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = state.local2Name,
                                onValueChange = { viewModel.onLocal2NameChange(it) },
                                label = { Text("Local 2 (Secundario)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Asignar Productos al Lote",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            if (state.availableProducts.isEmpty()) {
                item {
                    CustomExpressiveCard(position = CardPosition.SINGLE) {
                        Text(
                            text = "No tienes productos en el catálogo. Ve a 'Productos' para agregar algunos primero.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                itemsIndexed(state.availableProducts) { index, product ->
                    val pos = ExpressiveShapes.getPositionForIndex(index, state.availableProducts.size)
                    val pair = quantities[product.id] ?: Pair("", "")

                    CustomExpressiveCard(position = pos) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = CurrencyFormatter.format(product.price, state.currencySymbol),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            if (state.localCount == 1) {
                                OutlinedTextField(
                                    value = pair.first,
                                    onValueChange = { newVal ->
                                        quantities[product.id] = Pair(newVal, "0")
                                    },
                                    label = { Text("Cantidad de unidades") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = pair.first,
                                        onValueChange = { newVal ->
                                            quantities[product.id] = Pair(newVal, pair.second)
                                        },
                                        label = { Text("Cant. ${state.local1Name}") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = pair.second,
                                        onValueChange = { newVal ->
                                            quantities[product.id] = Pair(pair.first, newVal)
                                        },
                                        label = { Text("Cant. ${state.local2Name}") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
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
