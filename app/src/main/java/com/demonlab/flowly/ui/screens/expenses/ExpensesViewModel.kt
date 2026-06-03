package com.demonlab.flowly.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.ExpenseEntity
import com.demonlab.flowly.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExpensesUiState(
    val expenses: List<ExpenseEntity> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = true
)

class ExpensesViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _state = MutableStateFlow(ExpensesUiState())
    val state: StateFlow<ExpensesUiState> = _state.asStateFlow()

    init {
        loadCategories()
        loadExpenses()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { cats -> _state.value = _state.value.copy(categories = cats) }
        }
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            repository.getAllFlow().collect { expenses -> _state.value = _state.value.copy(expenses = expenses, isLoading = false) }
        }
    }

    fun filterByCategory(category: String?) {
        _state.value = _state.value.copy(selectedCategory = category)
        viewModelScope.launch {
            if (category != null) repository.getByCategoryFlow(category).collect { _state.value = _state.value.copy(expenses = it) }
            else loadExpenses()
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch { repository.getById(id)?.let { repository.delete(it) } }
    }

    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ExpensesViewModel(repository) as T
    }
}
