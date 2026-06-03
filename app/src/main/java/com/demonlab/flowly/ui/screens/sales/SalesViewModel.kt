package com.demonlab.flowly.ui.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.dao.SaleWithRecipe
import com.demonlab.flowly.data.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SalesUiState(
    val sales: List<SaleWithRecipe> = emptyList(),
    val isLoading: Boolean = true
)

class SalesViewModel(
    private val repository: SaleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SalesUiState())
    val state: StateFlow<SalesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSalesWithRecipeFlow().collect { sales ->
                _state.value = SalesUiState(sales = sales, isLoading = false)
            }
        }
    }

    fun deleteSale(id: Long) {
        viewModelScope.launch {
            val sale = repository.getById(id)
            if (sale != null) repository.delete(sale)
        }
    }

    class Factory(private val repository: SaleRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SalesViewModel(repository) as T
        }
    }
}
