package com.demonlab.flowly.ui.screens.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.InventoryItemEntity
import com.demonlab.flowly.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InventoryDetailState(
    val name: String = "",
    val quantity: String = "",
    val unit: String = "unidades",
    val unitPrice: String = "",
    val unitSize: String = "1",
    val category: String = "General",
    val minStock: String = "",
    val notes: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val purchasedQuantity: String = ""
) {
    val totalValue: Double
        get() = (quantity.toDoubleOrNull() ?: 0.0) * (unitPrice.toDoubleOrNull() ?: 0.0)
}

class InventoryDetailViewModel(
    private val repository: InventoryRepository,
    private val itemId: Long?
) : ViewModel() {

    private val _state = MutableStateFlow(InventoryDetailState())
    val state: StateFlow<InventoryDetailState> = _state.asStateFlow()

    init {
        if (itemId != null && itemId > 0) {
            loadItem(itemId)
        }
    }

    private fun loadItem(id: Long) {
        viewModelScope.launch {
            val item = repository.getById(id)
            if (item != null) {
                _state.value = InventoryDetailState(
                    name = item.name,
                    quantity = if (item.quantity == 0.0) "" else item.quantity.toString(),
                    unit = item.unit,
                    unitPrice = if (item.unitPrice == 0.0) "" else item.unitPrice.toString(),
                    unitSize = item.unitSize.toString(),
                    category = item.category,
                    minStock = item.minStock?.toString() ?: "",
                    notes = item.notes ?: "",
                    isEditing = true,
                    purchasedQuantity = if (item.purchasedQuantity == 0.0) "" else item.purchasedQuantity.toString()
                )
            }
        }
    }

    fun onNameChange(value: String) { _state.value = _state.value.copy(name = value) }
    fun onQuantityChange(value: String) { 
        val current = _state.value
        // If not editing, keep purchased quantity in sync with quantity by default
        _state.value = if (!current.isEditing) {
            current.copy(quantity = value, purchasedQuantity = value)
        } else {
            current.copy(quantity = value)
        }
    }
    fun onPurchasedQuantityChange(value: String) { _state.value = _state.value.copy(purchasedQuantity = value) }
    fun onUnitChange(value: String) { _state.value = _state.value.copy(unit = value) }
    fun onUnitPriceChange(value: String) { _state.value = _state.value.copy(unitPrice = value) }
    fun onUnitSizeChange(value: String) { _state.value = _state.value.copy(unitSize = value) }
    fun onCategoryChange(value: String) { _state.value = _state.value.copy(category = value) }
    fun onMinStockChange(value: String) { _state.value = _state.value.copy(minStock = value) }
    fun onNotesChange(value: String) { _state.value = _state.value.copy(notes = value) }

    fun save(onSuccess: () -> Unit) {
        val current = _state.value
        if (current.name.isBlank()) return

        _state.value = current.copy(isSaving = true)
        viewModelScope.launch {
            val qty = current.quantity.toDoubleOrNull() ?: 0.0
            val purchasedQty = current.purchasedQuantity.toDoubleOrNull() ?: qty
            repository.insert(
                InventoryItemEntity(
                    id = if (current.isEditing) itemId ?: 0 else 0,
                    name = current.name.trim(),
                    quantity = qty,
                    unit = current.unit.ifBlank { "unidades" },
                    unitPrice = current.unitPrice.toDoubleOrNull() ?: 0.0,
                    unitSize = current.unitSize.toDoubleOrNull() ?: 1.0,
                    category = current.category.ifBlank { "General" },
                    minStock = current.minStock.toDoubleOrNull(),
                    notes = current.notes.ifBlank { null },
                    purchasedQuantity = purchasedQty
                )
            )
            onSuccess()
        }
    }

    class Factory(
        private val repository: InventoryRepository,
        private val itemId: Long?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InventoryDetailViewModel(repository, itemId) as T
        }
    }
}
