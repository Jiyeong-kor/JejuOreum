package com.jeong.jejuoreum.data.local.permission

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

private val Context.permissionDataStore by preferencesDataStore(name = "permissions")

@Singleton
class PreferencePermissionLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : PermissionLocalDataSource {

    private val locationGrantedKey = booleanPreferencesKey("location_granted")

    override suspend fun setLocationPermissionGranted(granted: Boolean) {
        context.permissionDataStore.edit { preferences ->
            preferences[locationGrantedKey] = granted
        }
    }

    override suspend fun isLocationPermissionGranted(): Boolean {
        val preferences = context.permissionDataStore.data.first()
        return preferences[locationGrantedKey] ?: false
    }
}
