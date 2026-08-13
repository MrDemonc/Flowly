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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.demonlab.flowly.data.local.entity.BatchProductEntity

@Composable
fun RegisterSaleDialog(
    product: BatchProductEntity,
    local: String, // "LOCAL_1" or "LOCAL_2"
    localName: String,
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onConfirm: (quantity: Int, amountPaid: Double, amountPending: Double) -> Unit
) {
    val availableQty = if (local == "LOCAL_1") product.local1CurrentQty else product.local2CurrentQty
    var quantityText by remember { mutableStateOf("1") }
    var paidText by remember { mutableStateOf("") }
    var pendingText by remember { mutableStateOf("0") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Auto calculate default paid amount when quantity changes
    LaunchedEffect(quantityText) {
        val qty = quantityText.toIntOrNull() ?: 0
        if (qty > 0) {
            val totalExpected = qty * product.productPrice
            paidText = totalExpected.toString()
            pendingText = "0"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Registrar Venta ($localName)",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Precio: $currencySymbol ${product.productPrice} | Disponible: $availableQty un.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = {
                        quantityText = it
                        errorMessage = null
                    },
                    label = { Text("Cantidad vendida") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = paidText,
                    onValueChange = {
                        paidText = it
                        errorMessage = null
                    },
                    label = { Text("Monto Recaudado ($currencySymbol)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pendingText,
                    onValueChange = {
                        pendingText = it
                        errorMessage = null
                    },
                    label = { Text("Por Cobrar ($currencySymbol)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val qty = quantityText.toIntOrNull() ?: 0
                    val paid = paidText.toDoubleOrNull() ?: 0.0
                    val pending = pendingText.toDoubleOrNull() ?: 0.0

                    if (qty <= 0) {
                        errorMessage = "Ingrese una cantidad válida mayor a 0"
                    } else if (qty > availableQty) {
                        errorMessage = "La cantidad excede el stock disponible ($availableQty)"
                    } else if (paid < 0 || pending < 0) {
                        errorMessage = "Los montos no pueden ser negativos"
                    } else {
                        onConfirm(qty, paid, pending)
                    }
                }
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
