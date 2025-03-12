package com.jeong.jjoreum.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.jeong.jjoreum.util.PREF_KEY_NICKNAME

/**
 * 앱의 SharedPreferences를 관리하는 클래스
 */
class PreferenceManager private constructor() {

    companion object {
        @Volatile
        private var instance: com.jeong.jjoreum.data.local.PreferenceManager? = null
        private lateinit var sp: SharedPreferences
        private lateinit var spEditor: SharedPreferences.Editor

        /**
         * 싱글톤 인스턴스를 반환하는 함수
         * @param context 애플리케이션 컨텍스트
         * @return JJOreumPreferenceManager 인스턴스
         */
        fun getInstance(context: Context): com.jeong.jjoreum.data.local.PreferenceManager {
            return instance ?: synchronized(this) {
                instance ?: PreferenceManager().also {
                    sp = PreferenceManager.getDefaultSharedPreferences(context)
                    spEditor = sp.edit()
                    instance = it
                }
            }
        }
    }

    /**
     * 저장된 닉네임을 가져오는 함수
     * @return 저장된 닉네임 (없으면 빈 문자열 반환)
     */
    fun getNickname(): String = sp.getString(PREF_KEY_NICKNAME, "") ?: ""

    /**
     * 닉네임을 저장하는 함수
     * @param nickname 저장할 닉네임
     */
    fun setNickname(nickname: String) {
        spEditor.putString(PREF_KEY_NICKNAME, nickname).apply()
    }

    /**
     * 사용자가 등록된 상태인지 확인하는 함수
     * @return true: 등록됨, false: 미등록
     */
    fun isUserRegistered(): Boolean {
        return getNickname().isNotBlank()
    }

    /**
     * 사용자 데이터를 삭제하는 함수
     */
    fun clearUserData() {
        spEditor.remove(PREF_KEY_NICKNAME).apply()
    }
}