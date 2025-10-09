package com.jeong.jejuoreum.feature.onboarding.presentation

import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.ui.viewmodel.BaseViewModel
import com.jeong.jejuoreum.domain.user.model.NicknameValidationResult
import com.jeong.jejuoreum.feature.onboarding.domain.AnonymousUserInitializer
import com.jeong.jejuoreum.feature.onboarding.domain.NicknameAvailabilityChecker
import com.jeong.jejuoreum.feature.onboarding.domain.NicknameSaver
import com.jeong.jejuoreum.feature.onboarding.domain.NicknameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val nicknameValidator: NicknameValidator,
    private val nicknameAvailabilityChecker: NicknameAvailabilityChecker,
    private val nicknameSaver: NicknameSaver,
    private val anonymousUserInitializer: AnonymousUserInitializer,
) : BaseViewModel<JoinUiEvent, JoinSideEffect, JoinUiState>(JoinUiState()) {

    private var availabilityJob: Job? = null

    init {
        onEvent(JoinUiEvent.Initialize)
    }

    override fun onCleared() {
        availabilityJob?.cancel()
        availabilityJob = null
        super.onCleared()
    }

    override fun handleEvent(event: JoinUiEvent) {
        when (event) {
            JoinUiEvent.Initialize -> ensureAnonymousUser()
            is JoinUiEvent.NicknameChanged -> handleNicknameChanged(event.value)
            JoinUiEvent.SubmitNickname -> handleSubmit()
        }
    }

    private fun handleNicknameChanged(input: String) {
        val validationResult = nicknameValidator.validate(input)
        availabilityJob?.cancel()

        setState {
            val hasInteracted = hasUserInteracted || validationResult.value.isNotEmpty()
            copy(
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

    private fun handleSubmit() {
        val currentState = state.value
        if (!currentState.canSubmit) return

        viewModelScope.launch {
            setState { copy(isSaving = true) }
            val result = nicknameSaver.save(currentState.nickname)
            setState { copy(isSaving = false) }

            result.fold(
                onSuccess = { account ->
                    sendEffect { JoinSideEffect.NicknameSaved(account.nickname.orEmpty()) }
                },
                onFailure = { throwable ->
                    Timber.e(throwable, "Failed to save nickname")
                    sendEffect { JoinSideEffect.NicknameSaveFailed }
                }
            )
        }
    }

    private fun ensureAnonymousUser() {
        viewModelScope.launch {
            anonymousUserInitializer.ensure()
                .onFailure { throwable ->
                    Timber.e(throwable, "Failed to authenticate user")
                    sendEffect { JoinSideEffect.AuthenticationFailed }
                }
        }
    }

    private fun checkNicknameAvailability(nickname: String) {
        availabilityJob = viewModelScope.launch {
            val result = nicknameAvailabilityChecker.check(nickname)
            setState {
                if (this.nickname != nickname) {
                    this
                } else {
                    result.fold(
                        onSuccess = { isAvailable ->
                            if (isAvailable) {
                                copy(availability = NicknameAvailabilityState.Available)
                            } else {
                                copy(availability = NicknameAvailabilityState.Unavailable)
                            }
                        },
                        onFailure = { throwable ->
                            Timber.e(throwable, "Nickname availability check failed")
                            copy(availability = NicknameAvailabilityState.Error(throwable))
                        }
                    )
                }
            }
        }
    }
}
