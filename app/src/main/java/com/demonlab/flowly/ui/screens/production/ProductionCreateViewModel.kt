package com.demonlab.flowly.ui.screens.production

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.ProductionEntity
import com.demonlab.flowly.data.local.entity.RecipeEntity
import com.demonlab.flowly.data.repository.ProductionRepository
import com.demonlab.flowly.data.repository.RecipeRepository
import com.demonlab.flowly.domain.usecase.DeductInventoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ProductionCreateState(
    val recipes: List<RecipeEntity> = emptyList(),
    val selectedRecipeId: Long? = null,
    val quantity: String = "1",
    val totalCost: Double = 0.0,
    val notes: String = "",
    val isSaving: Boolean = false
)

class ProductionCreateViewModel(
    private val productionRepository: ProductionRepository,
    private val recipeRepository: RecipeRepository,
    private val deductInventoryUseCase: DeductInventoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductionCreateState())
    val state: StateFlow<ProductionCreateState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            recipeRepository.getAllFlow().collect { recipes ->
                _state.value = _state.value.copy(recipes = recipes.filter { it.isActive })
            }
        }
    }

    fun onRecipeSelected(recipeId: Long) {
        _state.value = _state.value.copy(selectedRecipeId = recipeId)
        recalculateCost()
    }

    fun onQuantityChange(value: String) {
        _state.value = _state.value.copy(quantity = value)
        recalculateCost()
    }

    fun onNotesChange(value: String) { _state.value = _state.value.copy(notes = value) }

    private fun recalculateCost() {
        val current = _state.value
        val recipe = current.recipes.find { it.id == current.selectedRecipeId }
        val qty = current.quantity.toDoubleOrNull() ?: 1.0
        if (recipe != null) {
            viewModelScope.launch {
                val realRecipeCost = recipeRepository.calculateRecipeCost(recipe.id)
                _state.value = _state.value.copy(totalCost = realRecipeCost * qty)
            }
        } else {
            _state.value = _state.value.copy(totalCost = 0.0)
        }
    }

    fun save(onSuccess: () -> Unit) {
        val current = _state.value
        val recipeId = current.selectedRecipeId ?: return
        val qty = current.quantity.toIntOrNull() ?: return

        _state.value = current.copy(isSaving = true)
        viewModelScope.launch {
            productionRepository.insert(
                ProductionEntity(
                    recipeId = recipeId,
                    quantity = qty,
                    totalCost = current.totalCost,
                    notes = current.notes.ifBlank { null }
                )
            )
            deductInventoryUseCase.execute(recipeId, qty)
            onSuccess()
        }
    }

    class Factory(
        private val productionRepository: ProductionRepository,
        private val recipeRepository: RecipeRepository,
        private val deductInventoryUseCase: DeductInventoryUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductionCreateViewModel(productionRepository, recipeRepository, deductInventoryUseCase) as T
        }
    }
}
