package com.demonlab.flowly.ui.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.RecipeEntity
import com.demonlab.flowly.data.local.entity.SaleEntity
import com.demonlab.flowly.data.repository.RecipeRepository
import com.demonlab.flowly.data.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SaleCreateState(
    val recipes: List<RecipeEntity> = emptyList(),
    val selectedRecipeId: Long? = null,
    val quantity: String = "1",
    val unitPrice: String = "",
    val totalAmount: Double = 0.0,
    val costAtSale: Double = 0.0,
    val paymentMethod: String = "Efectivo",
    val notes: String = "",
    val isSaving: Boolean = false
)

class SaleCreateViewModel(
    private val saleRepository: SaleRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SaleCreateState())
    val state: StateFlow<SaleCreateState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            recipeRepository.getAllFlow().collect { recipes ->
                _state.value = _state.value.copy(recipes = recipes.filter { it.isActive })
            }
        }
    }

    fun onRecipeSelected(recipeId: Long) {
        val recipe = _state.value.recipes.find { it.id == recipeId }
        _state.value = _state.value.copy(
            selectedRecipeId = recipeId,
            unitPrice = recipe?.salePrice?.toString() ?: ""
        )
        recalculate()
    }

    fun onQuantityChange(value: String) { _state.value = _state.value.copy(quantity = value); recalculate() }
    fun onUnitPriceChange(value: String) { _state.value = _state.value.copy(unitPrice = value); recalculate() }
    fun onPaymentMethodChange(value: String) { _state.value = _state.value.copy(paymentMethod = value) }
    fun onNotesChange(value: String) { _state.value = _state.value.copy(notes = value) }

    private fun recalculate() {
        val current = _state.value
        val qty = current.quantity.toDoubleOrNull() ?: 0.0
        val price = current.unitPrice.toDoubleOrNull() ?: 0.0
        val recipe = current.recipes.find { it.id == current.selectedRecipeId }
        val estimatedCost = (recipe?.salePrice ?: 0.0) * 0.5 * qty
        _state.value = current.copy(totalAmount = qty * price, costAtSale = estimatedCost)
    }

    fun save(onSuccess: () -> Unit) {
        val current = _state.value
        val recipeId = current.selectedRecipeId ?: return
        val qty = current.quantity.toIntOrNull() ?: return
        val price = current.unitPrice.toDoubleOrNull() ?: return

        _state.value = current.copy(isSaving = true)
        viewModelScope.launch {
            saleRepository.insert(
                SaleEntity(
                    recipeId = recipeId,
                    quantity = qty,
                    unitPrice = price,
                    totalAmount = current.totalAmount,
                    costAtSale = current.costAtSale,
                    paymentMethod = current.paymentMethod,
                    notes = current.notes.ifBlank { null }
                )
            )
            onSuccess()
        }
    }

    class Factory(
        private val saleRepository: SaleRepository,
        private val recipeRepository: RecipeRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SaleCreateViewModel(saleRepository, recipeRepository) as T
        }
    }
}
