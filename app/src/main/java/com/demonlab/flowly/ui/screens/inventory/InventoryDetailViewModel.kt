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
    val category: String = "General",
    val minStock: String = "",
    val notes: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false
)

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
                    category = item.category,
                    minStock = item.minStock?.toString() ?: "",
                    notes = item.notes ?: "",
                    isEditing = true
                )
            }
        }
    }

    fun onNameChange(value: String) { _state.value = _state.value.copy(name = value) }
    fun onQuantityChange(value: String) { _state.value = _state.value.copy(quantity = value) }
    fun onUnitChange(value: String) { _state.value = _state.value.copy(unit = value) }
    fun onCategoryChange(value: String) { _state.value = _state.value.copy(category = value) }
    fun onMinStockChange(value: String) { _state.value = _state.value.copy(minStock = value) }
    fun onNotesChange(value: String) { _state.value = _state.value.copy(notes = value) }

    fun save(onSuccess: () -> Unit) {
        val current = _state.value
        if (current.name.isBlank()) return

        _state.value = current.copy(isSaving = true)
        viewModelScope.launch {
            repository.insert(
                InventoryItemEntity(
                    id = if (current.isEditing) itemId ?: 0 else 0,
                    name = current.name.trim(),
                    quantity = current.quantity.toDoubleOrNull() ?: 0.0,
                    unit = current.unit.ifBlank { "unidades" },
                    category = current.category.ifBlank { "General" },
                    minStock = current.minStock.toDoubleOrNull(),
                    notes = current.notes.ifBlank { null }
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
