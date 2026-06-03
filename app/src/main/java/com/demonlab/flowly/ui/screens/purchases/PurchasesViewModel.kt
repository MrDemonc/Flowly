package com.demonlab.flowly.ui.screens.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.dao.PurchaseWithIngredient
import com.demonlab.flowly.data.repository.PurchaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PurchasesUiState(
    val purchases: List<PurchaseWithIngredient> = emptyList(),
    val isLoading: Boolean = true
)

class PurchasesViewModel(private val repository: PurchaseRepository) : ViewModel() {
    private val _state = MutableStateFlow(PurchasesUiState())
    val state: StateFlow<PurchasesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPurchasesWithIngredientFlow().collect { purchases ->
                _state.value = PurchasesUiState(purchases = purchases, isLoading = false)
            }
        }
    }

    fun deletePurchase(id: Long) {
        viewModelScope.launch {
            val purchase = repository.getById(id)
            if (purchase != null) repository.delete(purchase)
        }
    }

    class Factory(private val repository: PurchaseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = PurchasesViewModel(repository) as T
    }
}
