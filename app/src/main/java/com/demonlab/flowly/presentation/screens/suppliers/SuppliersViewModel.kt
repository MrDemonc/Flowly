package com.demonlab.flowly.presentation.screens.suppliers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.entity.SupplierEntity
import com.demonlab.flowly.data.repository.SupplierRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SuppliersUiState(
    val suppliers: List<SupplierEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class SuppliersViewModel(private val repository: SupplierRepository) : ViewModel() {
    private val _state = MutableStateFlow(SuppliersUiState())
    val state: StateFlow<SuppliersUiState> = _state.asStateFlow()
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            repository.getAllFlow().collect { suppliers ->
                _state.value = SuppliersUiState(suppliers = suppliers, isLoading = false)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            if (query.isNotEmpty()) repository.searchFlow(query).collect { _state.value = _state.value.copy(suppliers = it) }
            else repository.getAllFlow().collect { _state.value = _state.value.copy(suppliers = it) }
        }
    }

    fun delete(supplier: SupplierEntity) = viewModelScope.launch { repository.delete(supplier) }

    fun save(name: String, phone: String, address: String, notes: String) {
        viewModelScope.launch {
            repository.insert(SupplierEntity(name = name, phone = phone.ifBlank { null }, address = address.ifBlank { null }, notes = notes.ifBlank { null }))
        }
    }

    class Factory(private val repository: SupplierRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SuppliersViewModel(repository) as T
    }
}
