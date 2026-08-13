package com.demonlab.flowly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.demonlab.flowly.data.local.entity.ProductEntity

@Composable
fun AddProductDialog(
    initialProduct: ProductEntity? = null,
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: Double) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var priceText by remember { mutableStateOf(initialProduct?.price?.let { if (it == 0.0) "" else it.toString() } ?: "") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialProduct == null) "Agregar Producto" else "Editar Producto",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isError = false
                    },
                    label = { Text("Nombre del Producto") },
                    singleLine = true,
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Precio ($currencySymbol)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "El nombre no puede estar vacío",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        isError = true
                    } else {
                        val price = priceText.toDoubleOrNull() ?: 0.0
                        onConfirm(name.trim(), price)
                    }
                }
            ) {
                Text(if (initialProduct == null) "Guardar" else "Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
