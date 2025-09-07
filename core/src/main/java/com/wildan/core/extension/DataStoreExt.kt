package com.wildan.core.extension

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")
private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "authRemember")

private fun Context.getDataStore(useAuthStore: Boolean) =
    if (useAuthStore) authDataStore else appDataStore

suspend fun <T> Context.saveDataStore(key: String, value: T, useAuthStore: Boolean = false) {
    val store = getDataStore(useAuthStore)
    store.edit { preferences ->
        when (value) {
            is String -> preferences[stringPreferencesKey(key)] = value
            is Boolean -> preferences[booleanPreferencesKey(key)] = value
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }
}

suspend fun Context.getStringData(key: String, useAuthStore: Boolean = false): String {
    val store = getDataStore(useAuthStore)
    return store.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[stringPreferencesKey(key)] ?: "-"
        }
        .flowOn(Dispatchers.IO)
        .first()
}

suspend fun Context.getBooleanData(key: String): Boolean {
    return authDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: false
        }
        .flowOn(Dispatchers.IO)
        .first()
}

suspend fun Context.clearAppDataStore() {
    appDataStore.edit { it.clear() }
}

suspend fun Context.clearAuthDataStore() {
    authDataStore.edit { it.clear() }
}