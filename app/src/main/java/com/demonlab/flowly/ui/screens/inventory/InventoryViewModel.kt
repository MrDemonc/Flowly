package com.demonlab.flowly.ui.screens.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.InventoryItemEntity
import com.demonlab.flowly.data.repository.InventoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InventoryUiState(
    val items: List<InventoryItemEntity> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InventoryUiState())
    val state: StateFlow<InventoryUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCategories()
        loadItems()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _state.value = _state.value.copy(categories = categories)
            }
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            repository.getAllFlow().collect { items ->
                _state.value = _state.value.copy(items = items, isLoading = false)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            val currentState = _state.value
            if (currentState.searchQuery.isNotEmpty()) {
                repository.searchFlow(currentState.searchQuery).collect { items ->
                    _state.value = _state.value.copy(items = items)
                }
            } else {
                loadItems()
            }
        }
    }

    fun onCategorySelect(category: String?) {
        _state.value = _state.value.copy(selectedCategory = category)
        viewModelScope.launch {
            if (category != null) {
                repository.getByCategoryFlow(category).collect { items ->
                    _state.value = _state.value.copy(items = items)
                }
            } else {
                loadItems()
            }
        }
    }

    fun deleteItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    class Factory(private val repository: InventoryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InventoryViewModel(repository) as T
        }
    }
}
