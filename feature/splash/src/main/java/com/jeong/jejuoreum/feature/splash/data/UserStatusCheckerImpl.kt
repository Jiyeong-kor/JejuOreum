package com.jeong.feature.splash.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jeong.core.utils.PreferenceKeys.PREF_KEY_NICKNAME
import com.jeong.feature.splash.domain.UserStatusChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class UserStatusCheckerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : UserStatusChecker {
    private val dataStore = context.dataStore
    private val nicknameKey = stringPreferencesKey(PREF_KEY_NICKNAME)
    private val nicknameFlow = dataStore.data.map { preferences ->
        preferences[nicknameKey] ?: ""
    }

    override suspend fun isUserRegistered(): Boolean = nicknameFlow.first().isNotBlank()
}
