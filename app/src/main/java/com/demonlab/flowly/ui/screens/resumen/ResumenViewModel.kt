package com.demonlab.flowly.ui.screens.resumen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.datastore.SettingsDataStore
import com.demonlab.flowly.data.local.entity.BatchEntity
import com.demonlab.flowly.data.repository.BatchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

import com.demonlab.flowly.data.local.entity.BatchSaleEntity
import kotlinx.coroutines.launch

data class ResumenUiState(
    val totalPaid: Double = 0.0,
    val totalPending: Double = 0.0,
    val totalExpected: Double = 0.0,
    val currencySymbol: String = "$",
    val activeBatches: List<BatchEntity> = emptyList(),
    val pendingFiados: List<BatchSaleEntity> = emptyList(),
    val pendingFiadosCount: Int = 0,
    val totalBatchesCount: Int = 0
)

class ResumenViewModel(
    private val batchRepository: BatchRepository,
    settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<ResumenUiState> = combine(
        combine(
            batchRepository.totalPaidFlow,
            batchRepository.totalPendingFlow,
            settingsDataStore.currencySymbol
        ) { paid, pending, symbol -> Triple(paid, pending, symbol) },
        combine(
            batchRepository.batchesFlow,
            batchRepository.allPendingFiadosFlow,
            batchRepository.pendingFiadosCountFlow
        ) { batches, pendingFiados, count -> Triple(batches, pendingFiados, count) }
    ) { (paid, pending, symbol), (batches, pendingFiados, count) ->
        val paidAmount = paid ?: 0.0
        val pendingAmount = pending ?: 0.0
        ResumenUiState(
            totalPaid = paidAmount,
            totalPending = pendingAmount,
            totalExpected = paidAmount + pendingAmount,
            currencySymbol = symbol,
            activeBatches = batches.filter { it.status == "ACTIVO" },
            pendingFiados = pendingFiados,
            pendingFiadosCount = count,
            totalBatchesCount = batches.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResumenUiState()
    )

    fun toggleFiadoPaid(saleId: Long) {
        viewModelScope.launch {
            batchRepository.toggleFiadoPaid(saleId)
        }
    }

    class Factory(
        private val batchRepository: BatchRepository,
        private val settingsDataStore: SettingsDataStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ResumenViewModel(batchRepository, settingsDataStore) as T
        }
    }
}
