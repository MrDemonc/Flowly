package com.demonlab.flowly.ui.screens.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.RecipeEntity
import com.demonlab.flowly.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipeCreateState(
    val name: String = "",
    val description: String = "",
    val servings: String = "1",
    val instructions: String = "",
    val salePrice: String = "",
    val isSaving: Boolean = false
)

class RecipeCreateViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeCreateState())
    val state: StateFlow<RecipeCreateState> = _state.asStateFlow()

    fun onNameChange(value: String) { _state.value = _state.value.copy(name = value) }
    fun onDescriptionChange(value: String) { _state.value = _state.value.copy(description = value) }
    fun onServingsChange(value: String) { _state.value = _state.value.copy(servings = value) }
    fun onInstructionsChange(value: String) { _state.value = _state.value.copy(instructions = value) }
    fun onSalePriceChange(value: String) { _state.value = _state.value.copy(salePrice = value) }

    fun save(onSuccess: (Long) -> Unit) {
        val current = _state.value
        if (current.name.isBlank()) return

        _state.value = current.copy(isSaving = true)
        viewModelScope.launch {
            val id = repository.insert(
                RecipeEntity(
                    name = current.name.trim(),
                    description = current.description.ifBlank { null },
                    servings = current.servings.toIntOrNull() ?: 1,
                    instructions = current.instructions.ifBlank { null },
                    salePrice = current.salePrice.toDoubleOrNull() ?: 0.0
                )
            )
            onSuccess(id)
        }
    }

    class Factory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipeCreateViewModel(repository) as T
        }
    }
}
