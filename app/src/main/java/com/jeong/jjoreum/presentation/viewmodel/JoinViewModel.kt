package com.jeong.jjoreum.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.data.model.entity.JoinItem
import com.jeong.jjoreum.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

private val NICKNAME_REGEX = Regex("^[가-힣a-zA-Z0-9]+$")

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val prefs: PreferenceManager,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @param:ApplicationContext private val context: Context
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

        val isValid = nickname.length in 3..15 && nickname.matches(NICKNAME_REGEX)
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
            Timber.d(
                "checkNicknameAvailability - Current User UID: %s",
                currentUser?.uid
            )


            if (currentUser == null) {
                Timber.e("checkNicknameAvailability - User not authenticated!")
                _nicknameErrorMessage.value = context.getString(R.string.auth_error)
                _isNicknameAvailable.value = false
                _isLoadingNicknameAvailability.value = false
                return@launch
            }

            try {
                val snapshot = firestore.collection(Constants.COLLECTION_USER_INFO)
                    .whereEqualTo(Constants.FIELD_NICKNAME, nickname)
                    .get()
                    .await()

                if (snapshot.isEmpty) {
                    _nicknameErrorMessage.value = null
                    _isNicknameAvailable.value = true
                } else {
                    _nicknameErrorMessage.value =
                        context.getString(R.string.nickname_already_exists)
                    _isNicknameAvailable.value = false
                }
            } catch (e: Exception) {
                Timber.e(
                    e,
                    "Error checking nickname availability: %s", e.message
                )
                _nicknameErrorMessage.value = context.getString(R.string.nickname_check_error)
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
            Timber.w(
                "saveNickname - Nickname is empty, invalid, not available, or still loading."
            )
            onFailure()
            return
        }

        viewModelScope.launch {
            val currentUser = auth.currentUser
            Timber.d(
                "saveNickname - Current User UID: %s",
                currentUser?.uid
            )

            if (currentUser == null) {
                Timber.e("saveNickname - User not authenticated")
                onFailure()
                return@launch
            }

            val uid = currentUser.uid

            try {
                val userInfo = JoinItem(
                    uid = uid,
                    nickname = nickname
                )

                firestore.collection(Constants.COLLECTION_USER_INFO)
                    .document(uid)
                    .set(userInfo.toMap())
                    .await()

                Timber.i(
                    "Nickname successfully saved to Firestore for UID: %s",
                    uid
                )
                prefs.setNickname(nickname)
                onSuccess(nickname)
            } catch (e: Exception) {
                Timber.e(
                    e,
                    "Error saving nickname to Firestore for UID: %s",
                    uid
                )
                onFailure()
            }
        }
    }
}
