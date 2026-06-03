package com.demonlab.flowly.ui.screens.expenses

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
fun ExpenseCreateScreen(app: FlowlyApp, navController: NavController, viewModel: ExpenseCreateViewModel = viewModel(factory = ExpenseCreateViewModel.Factory(app.expenseRepository))) {
    val state by viewModel.state.collectAsState()
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("General", "Servicios", "Alquiler", "Marketing", "Transporte", "Empaque", "Mantenimiento", "Otro")

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Nuevo Gasto", style = MaterialTheme.typography.titleLarge) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            OutlinedTextField(value = state.description, onValueChange = viewModel::onDescriptionChange, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.amount, onValueChange = viewModel::onAmountChange, label = { Text("Monto") }, prefix = { Text("${CurrencySymbol.current} ") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(12.dp))
            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
                OutlinedTextField(value = state.category, onValueChange = {}, readOnly = true, label = { Text("Categoría") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = MaterialTheme.shapes.medium)
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    categories.forEach { cat -> DropdownMenuItem(text = { Text(cat) }, onClick = { viewModel.onCategoryChange(cat); categoryExpanded = false }) }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.notes, onValueChange = viewModel::onNotesChange, label = { Text("Notas (opcional)") }, modifier = Modifier.fillMaxWidth(), minLines = 2, shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(24.dp))
            Button(onClick = { viewModel.save { navController.popBackStack() } }, modifier = Modifier.fillMaxWidth(), enabled = state.description.isNotBlank() && state.amount.isNotBlank() && !state.isSaving, shape = MaterialTheme.shapes.medium) {
                Text(if (state.isSaving) "Guardando..." else "Registrar Gasto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
