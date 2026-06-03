package com.demonlab.flowly.ui.screens.purchases

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseCreateScreen(app: FlowlyApp, navController: NavController, viewModel: PurchaseCreateViewModel = viewModel(factory = PurchaseCreateViewModel.Factory(app.purchaseRepository, app.ingredientRepository))) {
    val state by viewModel.state.collectAsState()
    var ingredientExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Nueva Compra", style = MaterialTheme.typography.titleLarge) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            ExposedDropdownMenuBox(expanded = ingredientExpanded, onExpandedChange = { ingredientExpanded = !ingredientExpanded }) {
                OutlinedTextField(value = state.ingredients.find { it.id == state.selectedIngredientId }?.name ?: "", onValueChange = {}, readOnly = true, label = { Text("Ingrediente") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ingredientExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = MaterialTheme.shapes.medium)
                ExposedDropdownMenu(expanded = ingredientExpanded, onDismissRequest = { ingredientExpanded = false }) {
                    state.ingredients.forEach { ingredient -> DropdownMenuItem(text = { Text(ingredient.name) }, onClick = { viewModel.onIngredientSelected(ingredient.id); ingredientExpanded = false }) }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = state.quantity, onValueChange = viewModel::onQuantityChange, label = { Text("Cantidad") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, shape = MaterialTheme.shapes.medium)
                OutlinedTextField(value = state.unitCost, onValueChange = viewModel::onUnitCostChange, label = { Text("Costo uni.") }, prefix = { Text("${CurrencySymbol.current} ") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, shape = MaterialTheme.shapes.medium)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.supplier, onValueChange = viewModel::onSupplierChange, label = { Text("Proveedor (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.notes, onValueChange = viewModel::onNotesChange, label = { Text("Notas (opcional)") }, modifier = Modifier.fillMaxWidth(), minLines = 2, shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Total: ${CurrencySymbol.current} ${formatNumber(state.totalCost)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { viewModel.save { navController.popBackStack() } }, modifier = Modifier.fillMaxWidth(), enabled = state.selectedIngredientId != null && state.quantity.isNotBlank() && !state.isSaving, shape = MaterialTheme.shapes.medium) {
                Text(if (state.isSaving) "Guardando..." else "Registrar Compra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun formatNumber(value: Double) = java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "PY")).format(value)
