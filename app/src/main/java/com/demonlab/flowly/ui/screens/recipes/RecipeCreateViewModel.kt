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
    val servings: String = "1",
    val instructions: String = "",
    val salePrice: String = "",
    val isSaving: Boolean = false,
    val isEditing: Boolean = false
)

class RecipeCreateViewModel(
    private val repository: RecipeRepository,
    private val recipeId: Long? = null
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeCreateState())
    val state: StateFlow<RecipeCreateState> = _state.asStateFlow()

    init {
        if (recipeId != null && recipeId > 0) {
            loadRecipe(recipeId)
        }
    }

    private fun loadRecipe(id: Long) {
        viewModelScope.launch {
            val recipe = repository.getById(id)
            if (recipe != null) {
                _state.value = RecipeCreateState(
                    name = recipe.name,
                    servings = recipe.servings.toString(),
                    instructions = recipe.instructions ?: "",
                    salePrice = if (recipe.salePrice == 0.0) "" else recipe.salePrice.toString(),
                    isEditing = true
                )
            }
        }
    }

    fun onNameChange(value: String) { _state.value = _state.value.copy(name = value) }
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
                    id = if (current.isEditing) recipeId ?: 0 else 0,
                    name = current.name.trim(),
                    servings = current.servings.toIntOrNull() ?: 1,
                    instructions = current.instructions.ifBlank { null },
                    salePrice = current.salePrice.toDoubleOrNull() ?: 0.0
                )
            )
            onSuccess(id)
        }
    }

    class Factory(
        private val repository: RecipeRepository,
        private val recipeId: Long? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipeCreateViewModel(repository, recipeId) as T
        }
    }
}
