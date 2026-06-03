package com.demonlab.flowly.presentation.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.CategoryEntity
import com.demonlab.flowly.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = true
)

class CategoriesViewModel(private val repository: CategoryRepository) : ViewModel() {
    private val _state = MutableStateFlow(CategoriesUiState())
    val state: StateFlow<CategoriesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllFlow().collect { cats ->
                _state.value = CategoriesUiState(categories = cats, isLoading = false)
            }
        }
    }

    fun delete(category: CategoryEntity) {
        viewModelScope.launch { repository.delete(category) }
    }

    fun save(name: String, description: String, color: Long, id: Long = 0) {
        viewModelScope.launch {
            repository.insert(CategoryEntity(id = id, name = name, description = description.ifBlank { null }, color = color))
        }
    }

    class Factory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = CategoriesViewModel(repository) as T
    }
}
