package com.jeong.jejuoreum.feature.onboarding.presentation

import com.jeong.jejuoreum.core.ui.state.UiEffect
import com.jeong.jejuoreum.core.ui.state.UiEvent
import com.jeong.jejuoreum.core.ui.state.UiState
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

sealed interface JoinSideEffect : UiEffect {
    data object AuthenticationFailed : JoinSideEffect
    data class NicknameSaved(val nickname: String) : JoinSideEffect
    data object NicknameSaveFailed : JoinSideEffect
}
