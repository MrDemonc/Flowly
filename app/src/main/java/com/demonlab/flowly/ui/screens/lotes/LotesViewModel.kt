package com.demonlab.flowly.ui.screens.lotes

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
import kotlinx.coroutines.launch

data class LotesUiState(
    val batches: List<BatchEntity> = emptyList(),
    val currencySymbol: String = "$"
)

class LotesViewModel(
    private val batchRepository: BatchRepository,
    settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<LotesUiState> = combine(
        batchRepository.batchesFlow,
        settingsDataStore.currencySymbol
    ) { batches, symbol ->
        LotesUiState(batches = batches, currencySymbol = symbol)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LotesUiState()
    )

    fun deleteBatch(batch: BatchEntity) {
        viewModelScope.launch {
            batchRepository.deleteBatch(batch)
        }
    }

    class Factory(
        private val batchRepository: BatchRepository,
        private val settingsDataStore: SettingsDataStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LotesViewModel(batchRepository, settingsDataStore) as T
        }
    }
}
