package com.demonlab.flowly.ui.screens.recipes

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.data.local.dao.IngredientWithQuantity
import com.demonlab.flowly.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    app: FlowlyApp,
    navController: NavController,
    recipeId: Long = navController.currentBackStackEntry
        ?.arguments?.getLong("recipeId") ?: -1L,
    viewModel: RecipeDetailViewModel = viewModel(
        factory = RecipeDetailViewModel.Factory(
            app.recipeRepository,
            app.ingredientRepository,
            recipeId
        )
    )
) {
    if (recipeId == -1L) return
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = state.recipe?.name ?: "Receta",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (state.isLoading) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Precio Venta",
                            value = "${CurrencySymbol.current} ${formatNumber(state.recipe?.salePrice ?: 0.0)}",
                            modifier = Modifier.weight(1f),
                            valueColor = MaterialTheme.colorScheme.primary
                        )
                        StatCard(
                            title = "Costo Total",
                            value = "${CurrencySymbol.current} ${formatNumber(state.totalCost)}",
                            modifier = Modifier.weight(1f),
                            valueColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Ganancia x Porción",
                            value = "${CurrencySymbol.current} ${formatNumber(state.profitPerServing)}",
                            modifier = Modifier.weight(1f),
                            valueColor = if (state.profitPerServing >= 0)
                                MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.error
                        )
                        StatCard(
                            title = "Margen",
                            value = "${String.format("%.1f", state.margin)}%",
                            modifier = Modifier.weight(1f),
                            valueColor = if (state.margin >= 20)
                                MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    state.recipe?.let { recipe ->
                        recipe.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text(
                            text = "${recipe.servings} porciones",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ingredientes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(state.ingredients) { ingredient ->
                    IngredientCostCard(
                        ingredient = ingredient,
                        onRemove = { viewModel.removeIngredient(ingredient.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddIngredientDialog(
            ingredients = state.allIngredients,
            existingIds = state.ingredients.map { it.ingredientId }.toSet(),
            onAdd = { ingredientId, quantity ->
                viewModel.addIngredient(ingredientId, quantity)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun IngredientCostCard(
    ingredient: IngredientWithQuantity,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ingredient.ingredientName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${formatDecimal(ingredient.quantity)} ${ingredient.unit} x ${CurrencySymbol.current} ${formatNumber(ingredient.costPerUnit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${CurrencySymbol.current} ${formatNumber(ingredient.totalCost)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddIngredientDialog(
    ingredients: List<com.demonlab.flowly.data.local.entity.IngredientEntity>,
    existingIds: Set<Long>,
    onAdd: (Long, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedId by androidx.compose.runtime.remember { mutableStateOf<Long?>(null) }
    var quantityText by androidx.compose.runtime.remember { mutableStateOf("") }
    var dropdownExpanded by androidx.compose.runtime.remember { mutableStateOf(false) }
    val available = ingredients.filter { it.id !in existingIds }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Ingrediente", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                if (available.isEmpty()) {
                    Text("Todos los ingredientes ya están agregados")
                } else {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = available.find { it.id == selectedId }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ingrediente") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = MaterialTheme.shapes.medium
                        )
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            available.forEach { ingredient ->
                                DropdownMenuItem(
                                    text = { Text(ingredient.name) },
                                    onClick = { selectedId = ingredient.id; dropdownExpanded = false }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Cantidad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }
        },
        confirmButton = {
            if (selectedId != null && quantityText.toDoubleOrNull() != null) {
                TextButton(onClick = { onAdd(selectedId!!, quantityText.toDouble()) }) {
                    Text("Agregar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        shape = MaterialTheme.shapes.large
    )
}

private fun formatNumber(value: Double): String {
    return java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "PY")).format(value)
}

private fun formatDecimal(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format("%.2f", value)
    }
}
