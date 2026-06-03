package com.demonlab.flowly.ui.screens.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.IngredientEntity
import com.demonlab.flowly.data.repository.IngredientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IngredientDetailState(
    val name: String = "",
    val costPerUnit: String = "",
    val unit: String = "",
    val category: String = "",
    val notes: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false
)

class IngredientDetailViewModel(
    private val repository: IngredientRepository,
    private val ingredientId: Long?
) : ViewModel() {

    private val _state = MutableStateFlow(IngredientDetailState())
    val state: StateFlow<IngredientDetailState> = _state.asStateFlow()

    init {
        if (ingredientId != null && ingredientId > 0) {
            loadIngredient(ingredientId)
        }
    }

    private fun loadIngredient(id: Long) {
        viewModelScope.launch {
            val ingredient = repository.getById(id)
            if (ingredient != null) {
                _state.value = IngredientDetailState(
                    name = ingredient.name,
                    costPerUnit = if (ingredient.costPerUnit == 0.0) "" else ingredient.costPerUnit.toString(),
                    unit = ingredient.unit,
                    category = ingredient.category ?: "",
                    notes = ingredient.notes ?: "",
                    isEditing = true
                )
            }
        }
    }

    fun onNameChange(value: String) { _state.value = _state.value.copy(name = value) }
    fun onCostChange(value: String) { _state.value = _state.value.copy(costPerUnit = value) }
    fun onUnitChange(value: String) { _state.value = _state.value.copy(unit = value) }
    fun onCategoryChange(value: String) { _state.value = _state.value.copy(category = value) }
    fun onNotesChange(value: String) { _state.value = _state.value.copy(notes = value) }

    fun save(onSuccess: () -> Unit) {
        val current = _state.value
        if (current.name.isBlank()) return

        _state.value = current.copy(isSaving = true)
        viewModelScope.launch {
            repository.insert(
                IngredientEntity(
                    id = if (current.isEditing) ingredientId ?: 0 else 0,
                    name = current.name.trim(),
                    costPerUnit = current.costPerUnit.toDoubleOrNull() ?: 0.0,
                    unit = current.unit.ifBlank { "unidades" },
                    category = current.category.ifBlank { null },
                    notes = current.notes.ifBlank { null }
                )
            )
            onSuccess()
        }
    }

    class Factory(
        private val repository: IngredientRepository,
        private val ingredientId: Long?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IngredientDetailViewModel(repository, ingredientId) as T
        }
    }
}
