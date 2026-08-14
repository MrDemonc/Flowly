package com.demonlab.flowly.ui.screens.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.data.local.datastore.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AjustesUiState(
    val themeMode: String = "system",
    val currencySymbol: String = "$",
    val notifyPendingAccounts: Boolean = true,
    val localCount: Int = 2,
    val local1Name: String = "Local 1",
    val local2Name: String = "Local 2"
)

class AjustesViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<AjustesUiState> = combine(
        combine(
            settingsDataStore.themeMode,
            settingsDataStore.currencySymbol,
            settingsDataStore.notifyPendingAccounts
        ) { theme, currency, notify -> Triple(theme, currency, notify) },
        combine(
            settingsDataStore.localCount,
            settingsDataStore.local1Name,
            settingsDataStore.local2Name
        ) { count, l1, l2 -> Triple(count, l1, l2) }
    ) { (theme, currency, notify), (count, l1, l2) ->
        AjustesUiState(
            themeMode = theme,
            currencySymbol = currency,
            notifyPendingAccounts = notify,
            localCount = count,
            local1Name = l1,
            local2Name = l2
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AjustesUiState()
    )

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsDataStore.setThemeMode(mode)
        }
    }

    fun setCurrencySymbol(symbol: String) {
        viewModelScope.launch {
            if (symbol.isNotBlank()) {
                settingsDataStore.setCurrencySymbol(symbol.trim())
            }
        }
    }

    fun setNotifyPendingAccounts(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotifyPendingAccounts(enabled)
        }
    }

    fun setLocalCount(count: Int) {
        viewModelScope.launch {
            settingsDataStore.setLocalCount(count)
        }
    }

    fun setLocal1Name(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                settingsDataStore.setLocal1Name(name.trim())
            }
        }
    }

    fun setLocal2Name(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                settingsDataStore.setLocal2Name(name.trim())
            }
        }
    }

    class Factory(
        private val settingsDataStore: SettingsDataStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AjustesViewModel(settingsDataStore) as T
        }
    }
}
