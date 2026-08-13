package com.demonlab.flowly.ui.screens.lotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.demonlab.flowly.core.designsystem.ExpressiveShapes
import com.demonlab.flowly.data.local.entity.BatchEntity
import com.demonlab.flowly.navigation.Screen
import com.demonlab.flowly.ui.components.CircleIconBox
import com.demonlab.flowly.ui.components.CircleIconButton
import com.demonlab.flowly.ui.components.CustomExpressiveCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotesScreen(
    app: FlowlyApp,
    navController: NavController,
    viewModel: LotesViewModel = viewModel(
        factory = LotesViewModel.Factory(app.batchRepository, app.settingsDataStore)
    )
) {
    val state by viewModel.uiState.collectAsState()
    var batchToDelete by remember { mutableStateOf<BatchEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.LoteCreate.route) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo Lote")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
        ) {
            LargeTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleIconBox(
                            icon = Icons.Default.Layers,
                            contentDescription = "Lotes",
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            iconTint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.padding(6.dp))
                        Text(
                            text = "Lotes por Fecha",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            if (state.batches.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircleIconBox(
                        icon = Icons.Default.Layers,
                        contentDescription = null,
                        circleSize = 64.dp,
                        iconSize = 32.dp,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "No hay lotes registrados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Presiona el botón '+' para crear un nuevo lote.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(state.batches) { index, batch ->
                        val pos = ExpressiveShapes.getPositionForIndex(index, state.batches.size)
                        CustomExpressiveCard(
                            position = pos,
                            onClick = {
                                navController.navigate(Screen.LoteDetail.createRoute(batch.id))
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircleIconBox(
                                        icon = Icons.Default.Layers,
                                        contentDescription = "Lote",
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        iconTint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.padding(6.dp))
                                    Column {
                                        Text(
                                            text = "Lote ${batch.date}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${batch.local1Name} | ${batch.local2Name}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircleIconButton(
                                        icon = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        onClick = { batchToDelete = batch },
                                        circleSize = 36.dp,
                                        iconSize = 18.dp,
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        iconTint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    CircleIconBox(
                                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Detalles",
                                        circleSize = 36.dp,
                                        iconSize = 18.dp,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        batchToDelete?.let { batch ->
            AlertDialog(
                onDismissRequest = { batchToDelete = null },
                title = { Text("Eliminar Lote") },
                text = { Text("¿Deseas eliminar el lote del ${batch.date}?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteBatch(batch)
                            batchToDelete = null
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { batchToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
