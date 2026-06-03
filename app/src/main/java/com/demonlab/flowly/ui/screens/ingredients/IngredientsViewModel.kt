package com.demonlab.flowly.ui.screens.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.IngredientEntity
import com.demonlab.flowly.data.repository.IngredientRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IngredientsUiState(
    val ingredients: List<IngredientEntity> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class IngredientsViewModel(
    private val repository: IngredientRepository
) : ViewModel() {

    private val _state = MutableStateFlow(IngredientsUiState())
    val state: StateFlow<IngredientsUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCategories()
        loadIngredients()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _state.value = _state.value.copy(categories = categories)
            }
        }
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            repository.getAllFlow().collect { ingredients ->
                _state.value = _state.value.copy(ingredients = ingredients, isLoading = false)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            if (query.isNotEmpty()) {
                repository.searchFlow(query).collect { ingredients ->
                    _state.value = _state.value.copy(ingredients = ingredients)
                }
            } else {
                loadIngredients()
            }
        }
    }

    fun onCategorySelect(category: String?) {
        _state.value = _state.value.copy(selectedCategory = category)
    }

    fun deleteIngredient(ingredient: IngredientEntity) {
        viewModelScope.launch { repository.delete(ingredient) }
    }

    class Factory(private val repository: IngredientRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IngredientsViewModel(repository) as T
        }
    }
}
