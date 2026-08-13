package com.demonlab.flowly.ui.screens.productos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.datastore.SettingsDataStore
import com.demonlab.flowly.data.local.entity.ProductEntity
import com.demonlab.flowly.data.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductosUiState(
    val products: List<ProductEntity> = emptyList(),
    val currencySymbol: String = "$"
)

class ProductosViewModel(
    private val productRepository: ProductRepository,
    settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<ProductosUiState> = combine(
        productRepository.productsFlow,
        settingsDataStore.currencySymbol
    ) { products, symbol ->
        ProductosUiState(products = products, currencySymbol = symbol)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductosUiState()
    )

    fun addProduct(name: String, price: Double) {
        viewModelScope.launch {
            if (name.isNotBlank() && price >= 0) {
                productRepository.insert(name.trim(), price)
            }
        }
    }

    fun updateProduct(product: ProductEntity, newName: String, newPrice: Double) {
        viewModelScope.launch {
            if (newName.isNotBlank() && newPrice >= 0) {
                productRepository.update(product.copy(name = newName.trim(), price = newPrice))
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.delete(product)
        }
    }

    class Factory(
        private val productRepository: ProductRepository,
        private val settingsDataStore: SettingsDataStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductosViewModel(productRepository, settingsDataStore) as T
        }
    }
}
