package com.demonlab.flowly.ui.screens.lotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.datastore.SettingsDataStore
import com.demonlab.flowly.data.local.entity.ProductEntity
import com.demonlab.flowly.data.repository.BatchProductInput
import com.demonlab.flowly.data.repository.BatchRepository
import com.demonlab.flowly.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SelectedProductInput(
    val product: ProductEntity,
    var local1Qty: Int = 0,
    var local2Qty: Int = 0
)

data class LoteCreateUiState(
    val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
    val local1Name: String = "Local 1",
    val local2Name: String = "Local 2",
    val availableProducts: List<ProductEntity> = emptyList(),
    val currencySymbol: String = "$",
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class LoteCreateViewModel(
    private val batchRepository: BatchRepository,
    private val productRepository: ProductRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoteCreateUiState())
    val uiState: StateFlow<LoteCreateUiState> = _uiState.asStateFlow()

    private val selectedProductsMap = mutableMapOf<Long, Pair<Int, Int>>()

    init {
        viewModelScope.launch {
            val symbol = settingsDataStore.currencySymbol.first()
            val l1 = settingsDataStore.local1Name.first()
            val l2 = settingsDataStore.local2Name.first()
            val products = productRepository.productsFlow.first()

            _uiState.value = _uiState.value.copy(
                currencySymbol = symbol,
                local1Name = l1,
                local2Name = l2,
                availableProducts = products
            )
        }
    }

    fun onDateChange(newDate: String) {
        _uiState.value = _uiState.value.copy(date = newDate)
    }

    fun onLocal1NameChange(newName: String) {
        _uiState.value = _uiState.value.copy(local1Name = newName)
    }

    fun onLocal2NameChange(newName: String) {
        _uiState.value = _uiState.value.copy(local2Name = newName)
    }

    fun setProductQuantity(productId: Long, local1Qty: Int, local2Qty: Int) {
        selectedProductsMap[productId] = Pair(local1Qty.coerceAtLeast(0), local2Qty.coerceAtLeast(0))
    }

    fun getProductQuantity(productId: Long): Pair<Int, Int> {
        return selectedProductsMap[productId] ?: Pair(0, 0)
    }

    fun saveBatch(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.date.isBlank()) {
                _uiState.value = state.copy(errorMessage = "Ingrese una fecha válida")
                return@launch
            }

            val products = productRepository.productsFlow.first()
            val items = mutableListOf<BatchProductInput>()

            selectedProductsMap.forEach { (productId, quantities) ->
                val (l1Qty, l2Qty) = quantities
                if (l1Qty > 0 || l2Qty > 0) {
                    val product = products.find { it.id == productId }
                    if (product != null) {
                        items.add(
                            BatchProductInput(
                                productId = product.id,
                                productName = product.name,
                                productPrice = product.price,
                                local1Qty = l1Qty,
                                local2Qty = l2Qty
                            )
                        )
                    }
                }
            }

            if (items.isEmpty()) {
                _uiState.value = state.copy(errorMessage = "Agregue al menos un producto a un local")
                return@launch
            }

            val batchId = batchRepository.createBatch(
                date = state.date.trim(),
                local1Name = state.local1Name.ifBlank { "Local 1" },
                local2Name = state.local2Name.ifBlank { "Local 2" },
                items = items
            )
            _uiState.value = state.copy(isSuccess = true)
            onSuccess(batchId)
        }
    }

    class Factory(
        private val batchRepository: BatchRepository,
        private val productRepository: ProductRepository,
        private val settingsDataStore: SettingsDataStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoteCreateViewModel(batchRepository, productRepository, settingsDataStore) as T
        }
    }
}
