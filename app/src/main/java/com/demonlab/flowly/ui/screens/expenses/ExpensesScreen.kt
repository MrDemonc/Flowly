package com.demonlab.flowly.ui.screens.expenses

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.data.local.entity.ExpenseEntity
import com.demonlab.flowly.navigation.Screen
import com.demonlab.flowly.ui.components.ConfirmDialog
import com.demonlab.flowly.ui.components.EmptyState
import com.demonlab.flowly.ui.components.FilterChipsRow
import com.demonlab.flowly.ui.components.SectionCard
import com.demonlab.flowly.ui.components.SectionPosition
import com.demonlab.flowly.ui.components.sectionPositionFromIndex
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(app: FlowlyApp, navController: NavController, viewModel: ExpensesViewModel = viewModel(factory = ExpensesViewModel.Factory(app.expenseRepository))) {
    val state by viewModel.state.collectAsState()
    var deleteTargetId by remember { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Gastos", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))
        FilterChipsRow(options = state.categories, selectedOption = state.selectedCategory, onOptionSelected = viewModel::filterByCategory, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        Box(Modifier.weight(1f)) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.expenses.isEmpty() -> EmptyState(icon = Icons.Default.Receipt, title = "Sin gastos", description = "Registra tus gastos operativos")
                else -> LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    itemsIndexed(state.expenses, key = { _, it -> it.id }) { index, expense -> ExpenseCard(position = sectionPositionFromIndex(index, state.expenses.size), expense = expense, onDelete = { deleteTargetId = expense.id }) }
                }
            }

            FloatingActionButton(
                onClick = { navController.navigate(Screen.ExpenseCreate.route) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo gasto")
            }
        }
    }

    deleteTargetId?.let { id -> ConfirmDialog(title = "Eliminar gasto", message = "Esta acción no se puede deshacer", onConfirm = { viewModel.deleteExpense(id); deleteTargetId = null }, onDismiss = { deleteTargetId = null }) }
}

@Composable
private fun ExpenseCard(expense: ExpenseEntity, position: SectionPosition, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "PY")) }
    SectionCard(position = position) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(expense.description, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(expense.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Text(dateFormat.format(Date(expense.date)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
            Text("${CurrencySymbol.current} ${formatNumber(expense.amount)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onDelete) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
        }
    }
}

private fun formatNumber(value: Double) = java.text.NumberFormat.getNumberInstance(Locale("es", "PY")).format(value)
