package com.jeong.jjoreum.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.jeong.jjoreum.util.PREF_KEY_NICKNAME

/**
 * 앱의 SharedPreferences를 관리하는 클래스
 */
class PreferenceManager(context: Context) {

    private val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val spEditor: SharedPreferences.Editor = sp.edit()

    fun getNickname(): String = sp.getString(PREF_KEY_NICKNAME, "") ?: ""

    fun setNickname(nickname: String) {
        spEditor.putString(PREF_KEY_NICKNAME, nickname).apply()
    }

    fun isUserRegistered(): Boolean {
        return getNickname().isNotBlank()
    }

    fun clearUserData() {
        spEditor.remove(PREF_KEY_NICKNAME).apply()
    }
}