package com.demonlab.flowly.presentation.screens.suppliers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliersScreen(
    app: FlowlyApp,
    viewModel: SuppliersViewModel = viewModel(factory = SuppliersViewModel.Factory(app.supplierRepository))
) {
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Proveedores", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))

        OutlinedTextField(value = state.searchQuery, onValueChange = viewModel::onSearchQueryChange, placeholder = { Text("Buscar proveedores...") }, leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), singleLine = true, shape = MaterialTheme.shapes.medium)

        Box(Modifier.weight(1f)) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.suppliers.isEmpty() -> EmptyState(icon = Icons.Default.Person, title = "Sin proveedores", description = "Registra tus proveedores")
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.suppliers, key = { it.id }) { supplier ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(1.dp)) {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(supplier.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                                supplier.phone?.let { Text("Tel: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                supplier.address?.let { Text("Dir: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo Proveedor") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.save(name, phone, address, notes); showDialog = false; name = ""; phone = ""; address = ""; notes = "" }) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } },
            shape = MaterialTheme.shapes.large
        )
    }
}
