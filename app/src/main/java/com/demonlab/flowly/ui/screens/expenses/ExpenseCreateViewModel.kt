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

data class ExpenseCreateState(
    val description: String = "",
    val amount: String = "",
    val category: String = "General",
    val notes: String = "",
    val isSaving: Boolean = false
)

class ExpenseCreateViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _state = MutableStateFlow(ExpenseCreateState())
    val state: StateFlow<ExpenseCreateState> = _state.asStateFlow()

    fun onDescriptionChange(v: String) { _state.value = _state.value.copy(description = v) }
    fun onAmountChange(v: String) { _state.value = _state.value.copy(amount = v) }
    fun onCategoryChange(v: String) { _state.value = _state.value.copy(category = v) }
    fun onNotesChange(v: String) { _state.value = _state.value.copy(notes = v) }

    fun save(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.description.isBlank() || s.amount.isBlank()) return
        _state.value = s.copy(isSaving = true)
        viewModelScope.launch {
            repository.insert(ExpenseEntity(description = s.description.trim(), amount = s.amount.toDoubleOrNull() ?: 0.0, category = s.category, notes = s.notes.ifBlank { null }))
            onSuccess()
        }
    }

    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ExpenseCreateViewModel(repository) as T
    }
}
