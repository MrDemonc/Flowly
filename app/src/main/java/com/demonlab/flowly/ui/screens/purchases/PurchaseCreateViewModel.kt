package com.demonlab.flowly.ui.screens.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.IngredientEntity
import com.demonlab.flowly.data.local.entity.PurchaseEntity
import com.demonlab.flowly.data.repository.IngredientRepository
import com.demonlab.flowly.data.repository.PurchaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PurchaseCreateState(
    val ingredients: List<IngredientEntity> = emptyList(),
    val selectedIngredientId: Long? = null,
    val quantity: String = "",
    val unitCost: String = "",
    val totalCost: Double = 0.0,
    val supplier: String = "",
    val notes: String = "",
    val isSaving: Boolean = false
)

class PurchaseCreateViewModel(
    private val purchaseRepository: PurchaseRepository,
    private val ingredientRepository: IngredientRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PurchaseCreateState())
    val state: StateFlow<PurchaseCreateState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            ingredientRepository.getAllFlow().collect { ingredients ->
                _state.value = _state.value.copy(ingredients = ingredients)
            }
        }
    }

    fun onIngredientSelected(id: Long) { _state.value = _state.value.copy(selectedIngredientId = id); recalculate() }
    fun onQuantityChange(value: String) { _state.value = _state.value.copy(quantity = value); recalculate() }
    fun onUnitCostChange(value: String) { _state.value = _state.value.copy(unitCost = value); recalculate() }
    fun onSupplierChange(value: String) { _state.value = _state.value.copy(supplier = value) }
    fun onNotesChange(value: String) { _state.value = _state.value.copy(notes = value) }

    private fun recalculate() {
        val s = _state.value
        val qty = s.quantity.toDoubleOrNull() ?: 0.0
        val cost = s.unitCost.toDoubleOrNull() ?: 0.0
        _state.value = s.copy(totalCost = qty * cost)
    }

    fun save(onSuccess: () -> Unit) {
        val s = _state.value
        val ingredientId = s.selectedIngredientId ?: return
        val qty = s.quantity.toDoubleOrNull() ?: return
        val cost = s.unitCost.toDoubleOrNull() ?: return

        _state.value = s.copy(isSaving = true)
        viewModelScope.launch {
            purchaseRepository.insert(PurchaseEntity(ingredientId = ingredientId, quantity = qty, unitCost = cost, totalCost = s.totalCost, supplier = s.supplier.ifBlank { null }, notes = s.notes.ifBlank { null }))
            onSuccess()
        }
    }

    class Factory(private val purchaseRepository: PurchaseRepository, private val ingredientRepository: IngredientRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = PurchaseCreateViewModel(purchaseRepository, ingredientRepository) as T
    }
}
