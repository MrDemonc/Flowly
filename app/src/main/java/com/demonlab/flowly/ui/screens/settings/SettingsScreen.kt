package com.demonlab.flowly.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demonlab.flowly.FlowlyApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(app.settingsDataStore))
) {
    val state by viewModel.state.collectAsState()
    var marginText by remember(state.defaultMargin) { mutableStateOf(state.defaultMargin.toString()) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Configuración", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("Negocio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(value = state.businessName, onValueChange = viewModel::updateBusinessName, label = { Text("Nombre del negocio") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.currency, onValueChange = viewModel::updateCurrency, label = { Text("Moneda") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.medium)

            Spacer(Modifier.height(24.dp))
            Text("Costos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(value = marginText, onValueChange = { marginText = it; it.toDoubleOrNull()?.let { viewModel.updateDefaultMargin(it) } }, label = { Text("Margen de ganancia (%)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, shape = MaterialTheme.shapes.medium)

            Spacer(Modifier.height(24.dp))
            Text("Apariencia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Colores dinámicos (Material You)", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = state.dynamicColors, onCheckedChange = viewModel::toggleDynamicColors)
            }

            Spacer(Modifier.height(32.dp))

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Flowly v1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Desarrollado con \u2764 por DemonLab", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
