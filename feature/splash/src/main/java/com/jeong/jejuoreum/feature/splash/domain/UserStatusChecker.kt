package com.jeong.jejuoreum.feature.splash.domain

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")
private const val PREF_KEY_NICKNAME = "joinNickname"

@Singleton
class UserStatusChecker @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.dataStore
    private val nicknameKey = stringPreferencesKey(PREF_KEY_NICKNAME)
    private val nicknameFlow = dataStore.data.map { preferences ->
        preferences[nicknameKey] ?: ""
    }

    suspend fun isUserRegistered(): Boolean = nicknameFlow.first().isNotBlank()
}
