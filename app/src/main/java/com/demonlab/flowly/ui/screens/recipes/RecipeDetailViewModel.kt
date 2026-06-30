package com.demonlab.flowly.ui.screens.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.dao.IngredientWithQuantity
import com.demonlab.flowly.data.local.entity.IngredientEntity
import com.demonlab.flowly.data.local.entity.InventoryItemEntity
import com.demonlab.flowly.data.local.entity.RecipeEntity
import com.demonlab.flowly.data.local.entity.RecipeIngredientEntity
import com.demonlab.flowly.data.repository.IngredientRepository
import com.demonlab.flowly.data.repository.InventoryRepository
import com.demonlab.flowly.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

data class RecipeDetailState(
    val recipe: RecipeEntity? = null,
    val ingredients: List<IngredientWithQuantity> = emptyList(),
    val allIngredients: List<IngredientEntity> = emptyList(),
    val inventoryItems: List<InventoryItemEntity> = emptyList(),
    val totalCost: Double = 0.0,
    val profitPerServing: Double = 0.0,
    val margin: Double = 0.0,
    val isLoading: Boolean = true,
    val editingIngredientId: Long? = null
)

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
    private val inventoryRepository: InventoryRepository,
    private val recipeId: Long
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailState())
    val state: StateFlow<RecipeDetailState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val recipe = recipeRepository.getById(recipeId)
            if (recipe == null) {
                _state.value = _state.value.copy(isLoading = false)
                return@launch
            }

            combine(
                recipeRepository.getRecipeIngredientsWithDetailsFlow(recipeId),
                ingredientRepository.getAllFlow(),
                inventoryRepository.getAllFlow()
            ) { ingredients: List<IngredientWithQuantity>,
                allIngredients: List<IngredientEntity>,
                inventoryItems: List<InventoryItemEntity> ->

                val totalCost = ingredients.sumOf { it.totalCost }
                val costPerServing = if (recipe.servings > 0) totalCost / recipe.servings else totalCost
                val profit = recipe.salePrice - costPerServing
                val margin = if (recipe.salePrice > 0) (profit / recipe.salePrice) * 100 else 0.0

                _state.value = RecipeDetailState(
                    recipe = recipe,
                    ingredients = ingredients,
                    allIngredients = allIngredients,
                    inventoryItems = inventoryItems,
                    totalCost = totalCost,
                    profitPerServing = profit,
                    margin = margin,
                    isLoading = false
                )
            }.launchIn(viewModelScope)
        }
    }

    fun removeIngredient(relationId: Long) {
        viewModelScope.launch {
            recipeRepository.removeIngredientFromRecipe(relationId)
        }
    }

    fun addIngredient(ingredientId: Long, quantity: Double) {
        viewModelScope.launch {
            recipeRepository.addIngredientToRecipe(
                RecipeIngredientEntity(
                    recipeId = recipeId,
                    ingredientId = ingredientId,
                    quantity = quantity
                )
            )
        }
    }

    private fun getSubUnit(unit: String): String {
        return when (unit.lowercase()) {
            "kg" -> "g"
            "l" -> "mL"
            "paq" -> "unidades"
            "unidades" -> "unidades"
            else -> unit
        }
    }

    fun importFromInventory(inventoryItemId: Long, quantity: Double) {
        viewModelScope.launch {
            val inventoryItem = inventoryRepository.getById(inventoryItemId) ?: return@launch
            
            val subUnit = getSubUnit(inventoryItem.unit)
            val costPerSubUnit = if (inventoryItem.unitSize > 0) {
                inventoryItem.unitPrice / inventoryItem.unitSize
            } else {
                inventoryItem.unitPrice
            }

            val ingredientId = ingredientRepository.insert(
                IngredientEntity(
                    name = inventoryItem.name,
                    unit = subUnit,
                    costPerUnit = costPerSubUnit
                )
            )

            recipeRepository.addIngredientToRecipe(
                RecipeIngredientEntity(
                    recipeId = recipeId,
                    ingredientId = ingredientId,
                    quantity = quantity
                )
            )
        }
    }

    class Factory(
        private val recipeRepository: RecipeRepository,
        private val ingredientRepository: IngredientRepository,
        private val inventoryRepository: InventoryRepository,
        private val recipeId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipeDetailViewModel(recipeRepository, ingredientRepository, inventoryRepository, recipeId) as T
        }
    }
}
