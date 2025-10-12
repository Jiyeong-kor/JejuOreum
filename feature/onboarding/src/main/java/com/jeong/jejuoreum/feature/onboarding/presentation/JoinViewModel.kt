package com.jeong.jejuoreum.feature.onboarding.presentation

import com.jeong.jejuoreum.core.presentation.viewmodel.CommonBaseViewModel
import com.jeong.jejuoreum.domain.user.model.NicknameValidationResult
import com.jeong.jejuoreum.domain.user.usecase.CheckNicknameAvailabilityUseCase
import com.jeong.jejuoreum.domain.user.usecase.EnsureAnonymousUserUseCase
import com.jeong.jejuoreum.domain.user.usecase.SaveNicknameUseCase
import com.jeong.jejuoreum.domain.user.usecase.ValidateNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import timber.log.Timber

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
    private val checkNicknameAvailabilityUseCase: CheckNicknameAvailabilityUseCase,
    private val saveNicknameUseCase: SaveNicknameUseCase,
    private val ensureAnonymousUserUseCase: EnsureAnonymousUserUseCase,
    @Named("ioDispatcher") ioDispatcher: CoroutineDispatcher,
) : CommonBaseViewModel<JoinUiState, JoinUiEvent, JoinUiEffect>(ioDispatcher) {

    private var availabilityJob: Job? = null

    init {
        onEvent(JoinUiEvent.Initialize)
    }

    override fun onCleared() {
        availabilityJob?.cancel()
        availabilityJob = null
        super.onCleared()
    }

    override fun initialState(): JoinUiState = JoinUiState()

    override fun handleEvent(event: JoinUiEvent) {
        when (event) {
            JoinUiEvent.Initialize -> ensureAnonymousUser()
            is JoinUiEvent.NicknameChanged -> handleNicknameChanged(event.value)
            JoinUiEvent.SubmitNickname -> handleSubmit()
        }
    }

    private fun handleNicknameChanged(input: String) {
        val validationResult = validateNicknameUseCase(input)
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
        val latestState = currentState
        if (!latestState.canSubmit) return

        launch {
            setState { copy(isSaving = true) }
            val result = saveNicknameUseCase(latestState.nickname)
            setState { copy(isSaving = false) }

            result.fold(
                onSuccess = { account ->
                    sendEffect { JoinUiEffect.NicknameSaved(account.nickname.orEmpty()) }
                },
                onFailure = { throwable ->
                    Timber.e(throwable, "Failed to save nickname")
                    sendEffect { JoinUiEffect.NicknameSaveFailed }
                }
            )
        }
    }

    private fun ensureAnonymousUser() {
        launch {
            ensureAnonymousUserUseCase()
                .onFailure { throwable ->
                    Timber.e(throwable, "Failed to authenticate user")
                    sendEffect { JoinUiEffect.AuthenticationFailed }
                }
        }
    }

    private fun checkNicknameAvailability(nickname: String) {
        availabilityJob = launch {
            val result = checkNicknameAvailabilityUseCase(nickname)
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
