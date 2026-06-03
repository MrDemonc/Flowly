package com.demonlab.flowly.ui.screens.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.RecipeEntity
import com.demonlab.flowly.data.repository.RecipeRepository
import com.demonlab.flowly.domain.usecase.CalculateRecipeCostUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipesUiState(
    val recipes: List<RecipeEntity> = emptyList(),
    val costs: Map<Long, Double> = emptyMap(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class RecipesViewModel(
    private val repository: RecipeRepository,
    private val calculateRecipeCostUseCase: CalculateRecipeCostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RecipesUiState())
    val state: StateFlow<RecipesUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            repository.getAllFlow().collect { recipes ->
                _state.value = _state.value.copy(recipes = recipes, isLoading = false)
                computeCosts(recipes)
            }
        }
    }

    private fun computeCosts(recipes: List<RecipeEntity>) {
        recipes.forEach { recipe ->
            viewModelScope.launch {
                calculateRecipeCostUseCase.execute(recipe.id, recipe.salePrice)
                    .collect { cost ->
                        _state.value = _state.value.copy(
                            costs = _state.value.costs + (recipe.id to cost.totalCost)
                        )
                    }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            if (query.isNotEmpty()) {
                repository.searchFlow(query).collect { recipes ->
                    _state.value = _state.value.copy(recipes = recipes)
                    computeCosts(recipes)
                }
            } else {
                loadRecipes()
            }
        }
    }

    fun deleteRecipe(recipe: RecipeEntity) {
        viewModelScope.launch { repository.delete(recipe) }
    }

    class Factory(
        private val repository: RecipeRepository,
        private val calculateRecipeCostUseCase: CalculateRecipeCostUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipesViewModel(repository, calculateRecipeCostUseCase) as T
        }
    }
}
