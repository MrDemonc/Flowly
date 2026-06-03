package com.demonlab.flowly.ui.screens.production

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.dao.ProductionWithRecipe
import com.demonlab.flowly.data.repository.ProductionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductionUiState(
    val productions: List<ProductionWithRecipe> = emptyList(),
    val isLoading: Boolean = true
)

class ProductionViewModel(
    private val repository: ProductionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductionUiState())
    val state: StateFlow<ProductionUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getProductionsWithRecipeFlow().collect { productions ->
                _state.value = ProductionUiState(productions = productions, isLoading = false)
            }
        }
    }

    fun deleteProduction(id: Long) {
        viewModelScope.launch {
            val production = repository.getById(id)
            if (production != null) repository.delete(production)
        }
    }

    fun updateQuantity(id: Long, newQuantity: Int) {
        viewModelScope.launch {
            val production = repository.getById(id) ?: return@launch
            val oldCostPerBatch = production.totalCost / production.quantity
            val newTotalCost = oldCostPerBatch * newQuantity
            repository.update(production.copy(quantity = newQuantity, totalCost = newTotalCost))
        }
    }

    fun updateSold(id: Long, sold: Int) {
        viewModelScope.launch {
            repository.updateSold(id, sold.coerceAtLeast(0))
        }
    }

    class Factory(private val repository: ProductionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductionViewModel(repository) as T
        }
    }
}
