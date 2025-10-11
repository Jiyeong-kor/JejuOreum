package com.jeong.jejuoreum.feature.onboarding.presentation

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import com.jeong.jejuoreum.domain.user.model.NicknameValidationResult

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
) : UiState {
    val canSubmit: Boolean
        get() = availability is NicknameAvailabilityState.Available && !isSaving
}

sealed interface JoinUiEvent : UiEvent {
    data object Initialize : JoinUiEvent
    data class NicknameChanged(val value: String) : JoinUiEvent
    data object SubmitNickname : JoinUiEvent
}

sealed interface JoinUiEffect : UiEffect {
    data object AuthenticationFailed : JoinUiEffect
    data class NicknameSaved(val nickname: String) : JoinUiEffect
    data object NicknameSaveFailed : JoinUiEffect
}
