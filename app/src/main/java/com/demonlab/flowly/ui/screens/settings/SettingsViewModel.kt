package com.demonlab.flowly.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.demonlab.flowly.core.util.CurrencySymbol
import com.demonlab.flowly.data.local.datastore.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SettingsState(
    val businessName: String = "Flowly",
    val currency: String = CurrencySymbol.current,
    val defaultMargin: Double = 30.0,
    val dynamicColors: Boolean = true
)

class SettingsViewModel(private val dataStore: SettingsDataStore) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                dataStore.businessName,
                dataStore.currency,
                dataStore.defaultMargin,
                dataStore.dynamicColors
            ) { name, cur, margin, dyn ->
                SettingsState(businessName = name, currency = cur, defaultMargin = margin, dynamicColors = dyn)
            }.collect { _state.value = it }
        }
    }

    fun updateBusinessName(name: String) { viewModelScope.launch { dataStore.setBusinessName(name) } }
    fun updateCurrency(currency: String) { viewModelScope.launch { dataStore.setCurrency(currency) } }
    fun updateDefaultMargin(margin: Double) { viewModelScope.launch { dataStore.setDefaultMargin(margin) } }
    fun toggleDynamicColors(enabled: Boolean) { viewModelScope.launch { dataStore.setDynamicColors(enabled) } }

    class Factory(private val dataStore: SettingsDataStore) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(dataStore) as T
    }
}
