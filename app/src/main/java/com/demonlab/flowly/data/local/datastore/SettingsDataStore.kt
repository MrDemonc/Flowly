package com.demonlab.flowly.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val KEY_BUSINESS_NAME = stringPreferencesKey("business_name")
        private val KEY_CURRENCY = stringPreferencesKey("currency")
        private val KEY_DEFAULT_MARGIN = doublePreferencesKey("default_margin")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        private val KEY_IS_FIRST_RUN = booleanPreferencesKey("is_first_run")
    }

    val businessName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_BUSINESS_NAME] ?: "Flowly"
    }

    val currency: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CURRENCY] ?: "Gs"
    }

    val defaultMargin: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[KEY_DEFAULT_MARGIN] ?: 30.0
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "system"
    }

    val dynamicColors: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DYNAMIC_COLORS] ?: true
    }

    val isFirstRun: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_FIRST_RUN] ?: true
    }

    suspend fun setBusinessName(name: String) {
        context.dataStore.edit { it[KEY_BUSINESS_NAME] = name }
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { it[KEY_CURRENCY] = currency }
    }

    suspend fun setDefaultMargin(margin: Double) {
        context.dataStore.edit { it[KEY_DEFAULT_MARGIN] = margin }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DYNAMIC_COLORS] = enabled }
    }

    suspend fun setIsFirstRun(value: Boolean) {
        context.dataStore.edit { it[KEY_IS_FIRST_RUN] = value }
    }
}
