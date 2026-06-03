package com.demonlab.flowly.ui.screens.sales

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleCreateScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: SaleCreateViewModel = viewModel(
        factory = SaleCreateViewModel.Factory(app.saleRepository, app.recipeRepository)
    )
) {
    val state by viewModel.state.collectAsState()
    var recipeExpanded by remember { mutableStateOf(false) }
    var paymentExpanded by remember { mutableStateOf(false) }
    val paymentMethods = listOf("Efectivo", "Tarjeta", "Transferencia", "Otro")

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Nueva Venta", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            ExposedDropdownMenuBox(expanded = recipeExpanded, onExpandedChange = { recipeExpanded = !recipeExpanded }) {
                OutlinedTextField(
                    value = state.recipes.find { it.id == state.selectedRecipeId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true, label = { Text("Producto/Receta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recipeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(expanded = recipeExpanded, onDismissRequest = { recipeExpanded = false }) {
                    state.recipes.forEach { recipe ->
                        DropdownMenuItem(text = { Text(recipe.name) }, onClick = { viewModel.onRecipeSelected(recipe.id); recipeExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = state.quantity, onValueChange = viewModel::onQuantityChange, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, shape = MaterialTheme.shapes.medium)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = state.unitPrice, onValueChange = viewModel::onUnitPriceChange, label = { Text("Precio unitario") }, prefix = { Text("${CurrencySymbol.current} ") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, shape = MaterialTheme.shapes.medium)

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = paymentExpanded, onExpandedChange = { paymentExpanded = !paymentExpanded }) {
                OutlinedTextField(
                    value = state.paymentMethod, onValueChange = {},
                    readOnly = true, label = { Text("Método de pago") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(expanded = paymentExpanded, onDismissRequest = { paymentExpanded = false }) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(text = { Text(method) }, onClick = { viewModel.onPaymentMethodChange(method); paymentExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = state.notes, onValueChange = viewModel::onNotesChange, label = { Text("Notas (opcional)") }, modifier = Modifier.fillMaxWidth(), minLines = 2, shape = MaterialTheme.shapes.medium)

            Spacer(modifier = Modifier.height(16.dp))

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total: ${CurrencySymbol.current} ${formatNumber(state.totalAmount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.save { navController.popBackStack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.selectedRecipeId != null && state.unitPrice.isNotBlank() && !state.isSaving,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (state.isSaving) "Guardando..." else "Registrar Venta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun formatNumber(value: Double): String = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "PY")).format(value)
