package com.demonlab.flowly.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "flowly_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        private val KEY_NOTIFY_PENDING = booleanPreferencesKey("notify_pending")
        private val KEY_LOCAL1_NAME = stringPreferencesKey("local1_name")
        private val KEY_LOCAL2_NAME = stringPreferencesKey("local2_name")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "system"
    }

    val currencySymbol: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CURRENCY_SYMBOL] ?: "$"
    }

    val notifyPendingAccounts: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_NOTIFY_PENDING] ?: true
    }

    val local1Name: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LOCAL1_NAME] ?: "Local 1"
    }

    val local2Name: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LOCAL2_NAME] ?: "Local 2"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setCurrencySymbol(symbol: String) {
        context.dataStore.edit { it[KEY_CURRENCY_SYMBOL] = symbol }
    }

    suspend fun setNotifyPendingAccounts(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFY_PENDING] = enabled }
    }

    suspend fun setLocal1Name(name: String) {
        context.dataStore.edit { it[KEY_LOCAL1_NAME] = name }
    }

    suspend fun setLocal2Name(name: String) {
        context.dataStore.edit { it[KEY_LOCAL2_NAME] = name }
    }
}
