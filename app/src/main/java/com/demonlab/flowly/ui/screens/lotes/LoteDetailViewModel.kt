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
    val fiados: List<BatchSaleEntity> = emptyList(),
    val totalPaid: Double = 0.0,
    val totalPending: Double = 0.0,
    val totalExpected: Double = 0.0,
    val remainingUnits: Int = 0,
    val totalSoldUnits: Int = 0,
    val pendingFiadosCount: Int = 0,
    val localCount: Int = 2,
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
            batchRepository.getSalesByBatchIdFlow(batchId),
            batchRepository.getFiadosByBatchIdFlow(batchId)
        ) { batch, products, sales, fiados ->
            Quadruple(batch, products, sales, fiados)
        },
        combine(
            batchRepository.getBatchPaidFlow(batchId),
            batchRepository.getBatchPendingFlow(batchId),
            settingsDataStore.currencySymbol,
            settingsDataStore.localCount
        ) { paid, pending, symbol, count -> Quadruple(paid, pending, symbol, count) }
    ) { (batch, products, sales, fiados), (paid, pending, symbol, count) ->
        val paidAmount = paid ?: 0.0
        val pendingAmount = pending ?: 0.0
        val remaining = products.sumOf { it.local1CurrentQty + it.local2CurrentQty }
        val sold = sales.sumOf { it.quantity }
        val pendingFiados = fiados.count { !it.isPaid }

        LoteDetailUiState(
            batch = batch,
            products = products,
            sales = sales,
            fiados = fiados,
            totalPaid = paidAmount,
            totalPending = pendingAmount,
            totalExpected = paidAmount + pendingAmount,
            remainingUnits = remaining,
            totalSoldUnits = sold,
            pendingFiadosCount = pendingFiados,
            localCount = count,
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
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = batchRepository.registerSale(
                batchId = batchId,
                batchProductId = batchProductId,
                local = local,
                quantity = quantity,
                amountPaid = amountPaid
            )
            onResult(success)
        }
    }

    fun registerFiado(
        batchProductId: Long,
        local: String,
        quantity: Int,
        customerName: String,
        totalAmount: Double,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = batchRepository.registerFiado(
                batchId = batchId,
                batchProductId = batchProductId,
                local = local,
                quantity = quantity,
                customerName = customerName,
                totalAmount = totalAmount
            )
            onResult(success)
        }
    }

    fun toggleFiadoPaid(saleId: Long, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val success = batchRepository.toggleFiadoPaid(saleId)
            onResult(success)
        }
    }

    fun deleteSale(saleId: Long, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val success = batchRepository.deleteSale(saleId)
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

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
