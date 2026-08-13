package com.demonlab.flowly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.demonlab.flowly.data.local.entity.BatchProductEntity

@Composable
fun TransferDialog(
    product: BatchProductEntity,
    local1Name: String,
    local2Name: String,
    onDismiss: () -> Unit,
    onConfirm: (fromLocal: String, quantity: Int) -> Unit
) {
    var fromLocal by remember { mutableStateOf("LOCAL_1") } // "LOCAL_1" or "LOCAL_2"
    var quantityText by remember { mutableStateOf("1") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val availableQty = if (fromLocal == "LOCAL_1") product.local1CurrentQty else product.local2CurrentQty

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Transferir Producto",
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
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Origen:",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = fromLocal == "LOCAL_1",
                        onClick = {
                            fromLocal = "LOCAL_1"
                            errorMessage = null
                        }
                    )
                    Text(text = "$local1Name (Disp: ${product.local1CurrentQty})")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = fromLocal == "LOCAL_2",
                        onClick = {
                            fromLocal = "LOCAL_2"
                            errorMessage = null
                        }
                    )
                    Text(text = "$local2Name (Disp: ${product.local2CurrentQty})")
                }

                Spacer(modifier = Modifier.height(8.dp))
                val targetLocalName = if (fromLocal == "LOCAL_1") local2Name else local1Name
                Text(
                    text = "Hacia: $targetLocalName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = {
                        quantityText = it
                        errorMessage = null
                    },
                    label = { Text("Cantidad a transferir") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorMessage != null,
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
                    if (qty <= 0) {
                        errorMessage = "La cantidad debe ser mayor a 0"
                    } else if (qty > availableQty) {
                        errorMessage = "Cantidad excede el stock disponible ($availableQty)"
                    } else {
                        onConfirm(fromLocal, qty)
                    }
                }
            ) {
                Text("Transferir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
