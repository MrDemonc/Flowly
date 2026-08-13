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

data class ResumenUiState(
    val totalPaid: Double = 0.0,
    val totalPending: Double = 0.0,
    val totalExpected: Double = 0.0,
    val currencySymbol: String = "$",
    val activeBatches: List<BatchEntity> = emptyList(),
    val totalBatchesCount: Int = 0
)

class ResumenViewModel(
    batchRepository: BatchRepository,
    settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<ResumenUiState> = combine(
        batchRepository.totalPaidFlow,
        batchRepository.totalPendingFlow,
        settingsDataStore.currencySymbol,
        batchRepository.batchesFlow
    ) { paid, pending, symbol, batches ->
        val paidAmount = paid ?: 0.0
        val pendingAmount = pending ?: 0.0
        ResumenUiState(
            totalPaid = paidAmount,
            totalPending = pendingAmount,
            totalExpected = paidAmount + pendingAmount,
            currencySymbol = symbol,
            activeBatches = batches.filter { it.status == "ACTIVO" },
            totalBatchesCount = batches.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResumenUiState()
    )

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
