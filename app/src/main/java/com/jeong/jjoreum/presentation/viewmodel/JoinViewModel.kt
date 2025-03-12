package com.jeong.jjoreum.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.data.model.firestore.JoinItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * 회원가입 관련 로직을 처리하는 ViewModel
 * @param application 애플리케이션 컨텍스트
 */
class JoinViewModel(application: Application) : AndroidViewModel(application) {

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> get() = _nickname

    private val _nicknameError = MutableStateFlow<String?>(null)
    val nicknameError: StateFlow<String?> get() = _nicknameError

    private val _isNicknameAvailable = MutableStateFlow(false)
    val isNicknameAvailable: StateFlow<Boolean> get() = _isNicknameAvailable

    private val _nicknameErrorMessage = MutableStateFlow<String?>(null)
    val nicknameErrorMessage: StateFlow<String?> get() = _nicknameErrorMessage

    private val prefs = PreferenceManager.getInstance(application.applicationContext)

    /**
     * 닉네임을 업데이트하고 유효성을 검사하는 함수
     * @param input 사용자 입력 닉네임
     */
    fun updateNickname(input: String) {
        val nickname = input.trim()
        _nickname.value = nickname

        if (nickname.length !in 3..15 || !nickname.matches("^[가-힣a-zA-Z0-9]+$".toRegex())) {
            _nicknameError.value = "3~15자 한글, 영문, 숫자만 사용 가능"
            _isNicknameAvailable.value = false
        } else {
            checkNicknameAvailability(nickname)
        }
    }

    /**
     * Firestore에서 닉네임 중복 여부를 확인하는 함수
     * @param nickname 확인할 닉네임
     */
    private fun checkNicknameAvailability(nickname: String) {
        viewModelScope.launch {
            val snapshot = Firebase.firestore.collection("user_info_col")
                .whereEqualTo("nickname", nickname)
                .get()
                .await()

            if (snapshot.isEmpty) {
                _nicknameErrorMessage.value = null
                _isNicknameAvailable.value = true
            } else {
                _nicknameErrorMessage.value = "이미 존재하는 닉네임입니다."
                _isNicknameAvailable.value = false
            }
        }
    }

    /**
     * Firestore에 닉네임을 저장하는 함수
     * @param onSuccess 닉네임 저장 성공 시 호출할 콜백
     * @param onFailure 닉네임 저장 실패 시 호출할 콜백
     */
    fun saveNickname(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val nickname = _nickname.value.trim()
        if (!_isNicknameAvailable.value) return

        viewModelScope.launch {
            try {
                val userCollection = Firebase.firestore.collection("user_info_col")
                val nextUserId = getNextUserId(userCollection)

                val userInfo = JoinItem(
                    id = nextUserId,
                    nickname = nickname
                )

                userCollection.document(nextUserId.toString()).set(userInfo.toMap()).await()

                // SharedPreferences에 닉네임 저장
                prefs.setNickname(nickname)
                onSuccess(nickname)
            } catch (e: Exception) {
                onFailure()
            }
        }
    }

    /**
     * Firestore에서 다음 사용자 ID를 가져오는 함수
     * @param userCollection Firestore 컬렉션 참조
     * @return 다음 사용자 ID
     */
    private suspend fun getNextUserId(userCollection: com.google.firebase.firestore.CollectionReference): Int {
        val snapshot = userCollection.get().await()
        return (snapshot.size() + 1)
    }
}
