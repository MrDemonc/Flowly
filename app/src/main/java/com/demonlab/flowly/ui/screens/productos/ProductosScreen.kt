package com.demonlab.flowly.ui.screens.productos

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.core.designsystem.CardPosition
import com.demonlab.flowly.core.designsystem.ExpressiveShapes
import com.demonlab.flowly.core.util.CurrencyFormatter
import com.demonlab.flowly.data.local.entity.ProductEntity
import com.demonlab.flowly.ui.components.AddProductDialog
import com.demonlab.flowly.ui.components.CircleIconBox
import com.demonlab.flowly.ui.components.CircleIconButton
import com.demonlab.flowly.ui.components.CustomExpressiveCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    app: FlowlyApp,
    viewModel: ProductosViewModel = viewModel(
        factory = ProductosViewModel.Factory(app.productRepository, app.settingsDataStore)
    )
) {
    val state by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Productos",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { innerPadding ->
        if (state.products.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircleIconBox(
                    icon = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    circleSize = 64.dp,
                    iconSize = 32.dp,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No tienes productos guardados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Presiona el botón '+' para registrar tu primer producto.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                itemsIndexed(state.products) { index, product ->
                    val pos = ExpressiveShapes.getPositionForIndex(index, state.products.size)
                    CustomExpressiveCard(position = pos) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircleIconBox(
                                    icon = Icons.Default.ShoppingBag,
                                    contentDescription = product.name,
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    iconTint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.padding(6.dp))
                                Column {
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
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircleIconButton(
                                    icon = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    onClick = { productToEdit = product },
                                    circleSize = 36.dp,
                                    iconSize = 18.dp,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                                CircleIconButton(
                                    icon = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    onClick = { productToDelete = product },
                                    circleSize = 36.dp,
                                    iconSize = 18.dp,
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    iconTint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddProductDialog(
                currencySymbol = state.currencySymbol,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, price ->
                    viewModel.addProduct(name, price)
                    showAddDialog = false
                }
            )
        }

        productToEdit?.let { product ->
            AddProductDialog(
                initialProduct = product,
                currencySymbol = state.currencySymbol,
                onDismiss = { productToEdit = null },
                onConfirm = { name, price ->
                    viewModel.updateProduct(product, name, price)
                    productToEdit = null
                }
            )
        }

        productToDelete?.let { product ->
            AlertDialog(
                onDismissRequest = { productToDelete = null },
                title = { Text("Eliminar Producto") },
                text = { Text("¿Estás seguro de eliminar '${product.name}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteProduct(product)
                            productToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { productToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
