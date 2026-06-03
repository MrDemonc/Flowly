package com.demonlab.flowly.presentation.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    app: FlowlyApp,
    viewModel: CategoriesViewModel = viewModel(factory = CategoriesViewModel.Factory(app.categoryRepository))
) {
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editDesc by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Categorías", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Box(Modifier.weight(1f)) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (state.categories.isEmpty()) {
                EmptyState(icon = Icons.Default.Category, title = "Sin categorías", description = "Crea categorías para organizar tus productos")
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.categories, key = { it.id }) { cat ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(1.dp)) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(12.dp).padding(end = 12.dp).then(Modifier.size(12.dp))) {
                                    Surface(shape = MaterialTheme.shapes.small, color = Color(cat.color), modifier = Modifier.size(12.dp)) {}
                                }
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(cat.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                                    cat.description?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                }
                                IconButton(onClick = { viewModel.delete(cat) }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error) }
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
            title = { Text("Nueva Categoría") },
            text = {
                Column {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = editDesc, onValueChange = { editDesc = it }, label = { Text("Descripción (opcional)") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                }
            },
            confirmButton = {
                TextButton(onClick = { if (editName.isNotBlank()) { viewModel.save(editName, editDesc, 0xFF7A4B3A); showDialog = false; editName = ""; editDesc = "" } }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } },
            shape = MaterialTheme.shapes.large
        )
    }
}
