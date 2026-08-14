package com.demonlab.flowly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.demonlab.flowly.data.local.entity.BatchProductEntity

@Composable
fun RegisterFiadoDialog(
    product: BatchProductEntity,
    local: String, // "LOCAL_1" or "LOCAL_2"
    localName: String,
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onConfirm: (quantity: Int, customerName: String, totalAmount: Double) -> Unit
) {
    val availableQty = if (local == "LOCAL_1") product.local1CurrentQty else product.local2CurrentQty
    var customerName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1") }
    var totalAmountText by remember { mutableStateOf(product.productPrice.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(quantityText) {
        val qty = quantityText.toIntOrNull() ?: 0
        if (qty > 0) {
            totalAmountText = (qty * product.productPrice).toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Registrar Por Cobrar ($localName)",
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
                    text = "Precio: $currencySymbol ${product.productPrice} | Stock disponible: $availableQty un.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = {
                        customerName = it
                        errorMessage = null
                    },
                    label = { Text("Nombre del cliente / titular *") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = {
                        quantityText = it
                        errorMessage = null
                    },
                    label = { Text("Cantidad por cobrar") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = totalAmountText,
                    onValueChange = {
                        totalAmountText = it
                        errorMessage = null
                    },
                    label = { Text("Monto total por cobrar ($currencySymbol)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ℹ️ Este importe se registrará en 'Por Cobrar' hasta que el cliente efectúe el pago.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
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
                    val amount = totalAmountText.toDoubleOrNull() ?: 0.0

                    if (customerName.isBlank()) {
                        errorMessage = "Por favor ingrese el nombre del cliente"
                    } else if (qty <= 0) {
                        errorMessage = "Ingrese una cantidad válida mayor a 0"
                    } else if (qty > availableQty) {
                        errorMessage = "La cantidad excede el stock disponible ($availableQty)"
                    } else if (amount <= 0) {
                        errorMessage = "El monto debe ser mayor a 0"
                    } else {
                        onConfirm(qty, customerName.trim(), amount)
                    }
                }
            ) {
                Text("Registrar Por Cobrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
