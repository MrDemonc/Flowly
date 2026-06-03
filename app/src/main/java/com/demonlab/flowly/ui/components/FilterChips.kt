package com.demonlab.flowly.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipsRow(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    allLabel: String = "Todos"
) {
    val pillShape = RoundedCornerShape(50)

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        FilterChip(
            selected = selectedOption == null,
            onClick = { onOptionSelected(null) },
            label = { Text(allLabel) },
            shape = pillShape,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        options.forEach { option ->
            FilterChip(
                selected = selectedOption == option,
                onClick = {
                    onOptionSelected(if (selectedOption == option) null else option)
                },
                label = { Text(option) },
                shape = pillShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
