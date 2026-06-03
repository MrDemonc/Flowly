package com.demonlab.flowly.ui.screens.recipes

import com.demonlab.flowly.core.util.CurrencySymbol
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.demonlab.flowly.data.local.entity.RecipeEntity
import com.demonlab.flowly.navigation.Screen
import com.demonlab.flowly.ui.components.ConfirmDialog
import com.demonlab.flowly.ui.components.EmptyState
import com.demonlab.flowly.ui.components.SectionCard
import com.demonlab.flowly.ui.components.SectionPosition
import com.demonlab.flowly.ui.components.sectionPositionFromIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: RecipesViewModel = viewModel(
        factory = RecipesViewModel.Factory(app.recipeRepository)
    )
) {
    val state by viewModel.state.collectAsState()
    val collapseLimit = with(LocalDensity.current) { 88.dp.toPx() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(initialHeightOffset = -collapseLimit)
    )
    var deleteTarget by remember { mutableStateOf<RecipeEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LargeTopAppBar(
            title = {
                Text(
                    text = "Recetas",
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
        
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            placeholder = { Text("Buscar recetas...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.weight(1f).nestedScroll(scrollBehavior.nestedScrollConnection)) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.recipes.isEmpty() -> {
                    EmptyState(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        title = "Sin recetas",
                        description = "Crea tu primera receta con costeo automático"
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 400.dp)
                    ) {
                        itemsIndexed(state.recipes, key = { _, it -> it.id }) { index, recipe ->
                            RecipeCard(
                                position = sectionPositionFromIndex(index, state.recipes.size),
                                recipe = recipe,
                                onClick = {
                                    navController.navigate(Screen.RecipeDetail.createRoute(recipe.id))
                                },
                                onDelete = { deleteTarget = recipe }
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { navController.navigate(Screen.RecipeCreate.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva receta")
            }
        }
    }

    deleteTarget?.let { recipe ->
        ConfirmDialog(
            title = "Eliminar ${recipe.name}",
            message = "Esta acción no se puede deshacer",
            onConfirm = {
                viewModel.deleteRecipe(recipe)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun RecipeCard(
    recipe: RecipeEntity,
    position: SectionPosition,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    SectionCard(
        position = position,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDelete) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${recipe.servings} porciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${CurrencySymbol.current} ${formatNumber(recipe.salePrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun formatNumber(value: Double): String {
    return java.text.NumberFormat.getNumberInstance(java.util.Locale("es", "PY")).format(value)
}
