package com.demonlab.flowly.ui.screens.lotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.datastore.SettingsDataStore
import com.demonlab.flowly.data.local.entity.BatchEntity
import com.demonlab.flowly.data.local.entity.BatchProductEntity
import com.demonlab.flowly.data.local.entity.BatchSaleEntity
import com.demonlab.flowly.data.repository.BatchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LoteDetailUiState(
    val batch: BatchEntity? = null,
    val products: List<BatchProductEntity> = emptyList(),
    val sales: List<BatchSaleEntity> = emptyList(),
    val totalPaid: Double = 0.0,
    val totalPending: Double = 0.0,
    val totalExpected: Double = 0.0,
    val remainingUnits: Int = 0,
    val currencySymbol: String = "$"
)

class LoteDetailViewModel(
    private val batchId: Long,
    private val batchRepository: BatchRepository,
    settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<LoteDetailUiState> = combine(
        combine(
            batchRepository.getBatchByIdFlow(batchId),
            batchRepository.getProductsByBatchIdFlow(batchId),
            batchRepository.getSalesByBatchIdFlow(batchId)
        ) { batch, products, sales -> Triple(batch, products, sales) },
        combine(
            batchRepository.getBatchPaidFlow(batchId),
            batchRepository.getBatchPendingFlow(batchId),
            settingsDataStore.currencySymbol
        ) { paid, pending, symbol -> Triple(paid, pending, symbol) }
    ) { (batch, products, sales), (paid, pending, symbol) ->
        val paidAmount = paid ?: 0.0
        val pendingAmount = pending ?: 0.0
        val remaining = products.sumOf { it.local1CurrentQty + it.local2CurrentQty }

        LoteDetailUiState(
            batch = batch,
            products = products,
            sales = sales,
            totalPaid = paidAmount,
            totalPending = pendingAmount,
            totalExpected = paidAmount + pendingAmount,
            remainingUnits = remaining,
            currencySymbol = symbol
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoteDetailUiState()
    )

    fun transferProducts(batchProductId: Long, fromLocal: String, quantity: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = batchRepository.transferProducts(batchProductId, fromLocal, quantity)
            onResult(success)
        }
    }

    fun registerSale(
        batchProductId: Long,
        local: String,
        quantity: Int,
        amountPaid: Double,
        amountPending: Double,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = batchRepository.registerSale(
                batchId = batchId,
                batchProductId = batchProductId,
                local = local,
                quantity = quantity,
                amountPaid = amountPaid,
                amountPending = amountPending
            )
            onResult(success)
        }
    }

    class Factory(
        private val batchId: Long,
        private val batchRepository: BatchRepository,
        private val settingsDataStore: SettingsDataStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoteDetailViewModel(batchId, batchRepository, settingsDataStore) as T
        }
    }
}
