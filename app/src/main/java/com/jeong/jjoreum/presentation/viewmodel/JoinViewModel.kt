package com.jeong.jjoreum.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.data.model.entity.JoinItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val prefs: PreferenceManager,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _hasUserTyped = MutableStateFlow(false)
    val hasUserTyped: StateFlow<Boolean> get() = _hasUserTyped

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> get() = _nickname

    private val _isNicknameInvalid = MutableStateFlow(false)
    val isNicknameInvalid: StateFlow<Boolean> get() = _isNicknameInvalid

    private val _isNicknameAvailable = MutableStateFlow(false)
    val isNicknameAvailable: StateFlow<Boolean> get() = _isNicknameAvailable

    private val _nicknameErrorMessage = MutableStateFlow<String?>(null)
    val nicknameErrorMessage: StateFlow<String?> get() = _nicknameErrorMessage

    private val _isLoadingNicknameAvailability = MutableStateFlow(false)
    val isLoadingNicknameAvailability: StateFlow<Boolean> get() = _isLoadingNicknameAvailability

    fun updateNickname(input: String) {
        val nickname = input.trim()
        _nickname.value = nickname

        if (!_hasUserTyped.value && nickname.isNotEmpty()) {
            _hasUserTyped.value = true
        }

        if (nickname.isEmpty()) {
            _isNicknameInvalid.value = false
            _nicknameErrorMessage.value = null
            _isNicknameAvailable.value = false
            _isLoadingNicknameAvailability.value = false
            return
        }

        val isValid = nickname.length in 3..15 && nickname.matches("^[가-힣a-zA-Z0-9]+$".toRegex())
        _isNicknameInvalid.value = !isValid

        if (isValid) {
            checkNicknameAvailability(nickname)
        } else {
            _isNicknameAvailable.value = false
            _nicknameErrorMessage.value = null
            _isLoadingNicknameAvailability.value = false
        }
    }

    private fun checkNicknameAvailability(nickname: String) {
        _isLoadingNicknameAvailability.value = true
        _nicknameErrorMessage.value = null

        viewModelScope.launch {
            val currentUser = auth.currentUser
            Log.d(
                "JoinViewModel",
                "checkNicknameAvailability - Current User UID: ${currentUser?.uid}"
            )

            if (currentUser == null) {
                Log.e(
                    "JoinViewModel",
                    "checkNicknameAvailability - User not authenticated!"
                )
                _nicknameErrorMessage.value = "사용자 인증 오류"
                _isNicknameAvailable.value = false
                _isLoadingNicknameAvailability.value = false
                return@launch
            }

            try {
                val snapshot = firestore.collection("user_info_col")
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
            } catch (e: Exception) {
                Log.e(
                    "JoinViewModel",
                    "Error checking nickname availability: ${e.message}", e
                )
                _nicknameErrorMessage.value = "닉네임 확인 중 오류 발생"
                _isNicknameAvailable.value = false
            } finally {
                _isLoadingNicknameAvailability.value = false
            }
        }
    }

    fun saveNickname(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val nickname = _nickname.value.trim()
        if (nickname.isEmpty() || _isNicknameInvalid.value
            || !_isNicknameAvailable.value || _isLoadingNicknameAvailability.value
        ) {
            Log.w(
                "JoinViewModel",
                "saveNickname - Nickname is empty, invalid, not available, or still loading."
            )
            onFailure()
            return
        }

        viewModelScope.launch {
            val currentUser = auth.currentUser
            Log.d(
                "JoinViewModel",
                "saveNickname - Current User UID: ${currentUser?.uid}"
            )

            if (currentUser == null) {
                Log.e("JoinViewModel", "saveNickname - User not authenticated!")
                onFailure()
                return@launch
            }

            val uid = currentUser.uid

            try {
                val userInfo = JoinItem(
                    uid = uid,
                    nickname = nickname
                )

                firestore.collection("user_info_col")
                    .document(uid)
                    .set(userInfo.toMap())
                    .await()

                Log.i(
                    "JoinViewModel",
                    "Nickname successfully saved to Firestore for UID: $uid"
                )
                prefs.setNickname(nickname)
                onSuccess(nickname)
            } catch (e: Exception) {
                Log.e(
                    "JoinViewModel",
                    "Error saving nickname to Firestore for UID: $uid", e
                )
                onFailure()
            }
        }
    }
}
