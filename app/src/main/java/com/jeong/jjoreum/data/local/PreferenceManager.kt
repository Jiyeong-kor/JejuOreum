package com.jeong.jjoreum.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jeong.jjoreum.util.PREF_KEY_NICKNAME
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferenceManager(context: Context) {
    private val dataStore = context.dataStore
    private val nicknameKey = stringPreferencesKey(PREF_KEY_NICKNAME)
    val nicknameFlow = dataStore.data.map { it[nicknameKey] ?: "" }

    suspend fun getNickname(): String = nicknameFlow.first()

    suspend fun setNickname(nickname: String) {
        dataStore.edit { it[nicknameKey] = nickname }
    }

    suspend fun isUserRegistered(): Boolean = getNickname().isNotBlank()

    suspend fun clearUserData() {
        dataStore.edit { it.remove(nicknameKey) }
    }
}