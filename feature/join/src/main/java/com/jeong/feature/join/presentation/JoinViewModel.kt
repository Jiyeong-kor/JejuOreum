package com.jeong.feature.join.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.domain.model.NicknameValidationResult
import com.jeong.domain.usecase.CheckNicknameAvailabilityUseCase
import com.jeong.domain.usecase.EnsureAnonymousUserUseCase
import com.jeong.domain.usecase.SaveNicknameUseCase
import com.jeong.domain.usecase.ValidateNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
    private val checkNicknameAvailabilityUseCase: CheckNicknameAvailabilityUseCase,
    private val saveNicknameUseCase: SaveNicknameUseCase,
    private val ensureAnonymousUserUseCase: EnsureAnonymousUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinUiState())
    val uiState: StateFlow<JoinUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<JoinEvent>()
    val events: SharedFlow<JoinEvent> = _events.asSharedFlow()

    private var availabilityJob: Job? = null

    init {
        ensureAnonymousUser()
    }

    fun onNicknameChanged(input: String) {
        val validationResult = validateNicknameUseCase(input)
        availabilityJob?.cancel()

        _uiState.update { state ->
            val hasInteracted = state.hasUserInteracted || validationResult.value.isNotEmpty()
            state.copy(
                nickname = validationResult.value,
                hasUserInteracted = hasInteracted,
                validation = validationResult,
                availability = if (validationResult is NicknameValidationResult.Valid) {
                    NicknameAvailabilityState.Checking
                } else {
                    NicknameAvailabilityState.Idle
                }
            )
        }

        if (validationResult is NicknameValidationResult.Valid) {
            checkNicknameAvailability(validationResult.value)
        }
    }

    fun submitNickname() {
        val currentState = _uiState.value
        if (!currentState.canSubmit) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = saveNicknameUseCase(currentState.nickname)
            _uiState.update { it.copy(isSaving = false) }

            result.fold(
                onSuccess = { account ->
                    _events.emit(JoinEvent.NicknameSaved(account.nickname.orEmpty()))
                },
                onFailure = { throwable ->
                    Timber.e(throwable, "Failed to save nickname")
                    _events.emit(JoinEvent.NicknameSaveFailed)
                }
            )
        }
    }

    private fun ensureAnonymousUser() {
        viewModelScope.launch {
            ensureAnonymousUserUseCase()
                .onFailure { throwable ->
                    Timber.e(throwable, "Failed to authenticate user")
                    _events.emit(JoinEvent.AuthenticationFailed)
                }
        }
    }

    private fun checkNicknameAvailability(nickname: String) {
        availabilityJob = viewModelScope.launch {
            val result = checkNicknameAvailabilityUseCase(nickname)
            _uiState.update { state ->
                if (state.nickname != nickname) {
                    state
                } else {
                    result.fold(
                        onSuccess = { isAvailable ->
                            if (isAvailable) {
                                state.copy(availability = NicknameAvailabilityState.Available)
                            } else {
                                state.copy(availability = NicknameAvailabilityState.Unavailable)
                            }
                        },
                        onFailure = { throwable ->
                            Timber.e(throwable, "Nickname availability check failed")
                            state.copy(availability = NicknameAvailabilityState.Error(throwable))
                        }
                    )
                }
            }
        }
    }
}