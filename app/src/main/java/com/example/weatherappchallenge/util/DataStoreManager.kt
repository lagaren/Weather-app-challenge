package com.example.weatherappchallenge.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    //Get last city that the user searched for
    val getLastSearch: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PREVIOUS_SEARCH_KEY] ?: ""
    }

    //Save last city user searched for using data store
    suspend fun saveLastSearch(lastSearch: String) {
        context.dataStore.edit { preferences ->
            preferences[PREVIOUS_SEARCH_KEY] = lastSearch
        }
    }

    companion object {
        private val PREVIOUS_SEARCH_KEY = stringPreferencesKey("LAST_SEARCH")
        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "lastSearch")
    }
}