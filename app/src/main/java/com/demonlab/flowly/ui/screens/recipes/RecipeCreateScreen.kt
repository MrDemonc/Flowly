package com.demonlab.flowly.ui.screens.recipes

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCreateScreen(
    app: FlowlyApp,
    navController: NavController,
    recipeId: Long? = null,
    viewModel: RecipeCreateViewModel = viewModel(
        factory = RecipeCreateViewModel.Factory(app.recipeRepository, recipeId)
    )
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    if (state.isEditing) "Editar Receta" else "Nueva Receta",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre de la receta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.servings,
                onValueChange = viewModel::onServingsChange,
                label = { Text("Porciones") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.salePrice,
                onValueChange = viewModel::onSalePriceChange,
                label = { Text("Precio de venta") },
                prefix = { Text("${CurrencySymbol.current} ") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.instructions,
                onValueChange = viewModel::onInstructionsChange,
                label = { Text("Instrucciones (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.save { id ->
                        navController.navigate(Screen.RecipeDetail.createRoute(id)) {
                            popUpTo(Screen.RecipeCreate.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.name.isNotBlank() && !state.isSaving,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (state.isSaving) "Guardando..." else if (state.isEditing) "Guardar Cambios" else "Crear Receta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
