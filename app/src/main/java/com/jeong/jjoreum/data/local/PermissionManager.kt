package com.jeong.jjoreum.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "permissions")

object PermissionManager {
    private val locationGrantedKey = booleanPreferencesKey("location_granted")
    suspend fun setLocationGranted(context: Context, granted: Boolean) {
        context.dataStore.edit { it[locationGrantedKey] = granted }
    }

    suspend fun isLocationGranted(context: Context): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[locationGrantedKey] ?: false
    }
}