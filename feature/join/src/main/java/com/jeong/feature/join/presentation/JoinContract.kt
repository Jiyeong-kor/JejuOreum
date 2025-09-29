package com.jeong.feature.join.presentation

import com.jeong.domain.model.NicknameValidationResult

sealed interface NicknameAvailabilityState {
    data object Idle : NicknameAvailabilityState
    data object Checking : NicknameAvailabilityState
    data object Available : NicknameAvailabilityState
    data object Unavailable : NicknameAvailabilityState
    data class Error(val cause: Throwable? = null) : NicknameAvailabilityState
}

data class JoinUiState(
    val nickname: String = "",
    val hasUserInteracted: Boolean = false,
    val validation: NicknameValidationResult = NicknameValidationResult.Empty(),
    val availability: NicknameAvailabilityState = NicknameAvailabilityState.Idle,
    val isSaving: Boolean = false,
) {
    val canSubmit: Boolean
        get() = availability is NicknameAvailabilityState.Available && !isSaving
}

sealed interface JoinEvent {
    data object AuthenticationFailed : JoinEvent
    data class NicknameSaved(val nickname: String) : JoinEvent
    data object NicknameSaveFailed : JoinEvent
}
