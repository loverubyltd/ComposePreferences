package de.schnettler.datastore.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(val dataStore: DataStore<Preferences>) {
    val preferenceFlow = dataStore.data

    suspend fun <T> getPreference(preferenceEntry: PreferenceRequest<T>) =
        preferenceFlow.first()[preferenceEntry.key] ?: preferenceEntry.defaultValue

    fun <T> getPreferenceFlow(request: PreferenceRequest<T>) =
        preferenceFlow.map {
            it[request.key] ?: request.defaultValue
        }

    suspend fun <T> editPreference(key: Preferences.Key<T>, newValue: T) {
        dataStore.edit { preferences ->
            preferences[key] = newValue
        }
    }

    suspend fun clearPreferences() {
        dataStore.edit { preferences -> preferences.clear() }
    }
}