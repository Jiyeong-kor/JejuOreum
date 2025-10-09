package com.jeong.jejuoreum.app.presentation.main

import com.jeong.jejuoreum.core.ui.state.UiEffect
import com.jeong.jejuoreum.core.ui.state.UiEvent
import com.jeong.jejuoreum.core.ui.state.UiState
import com.jeong.jejuoreum.data.remote.NetworkStatus

data class MainUiState(
    val networkStatus: NetworkStatus = NetworkStatus.Unknown,
    val showNetworkDialog: Boolean = false
) : UiState {
    val isOffline: Boolean
        get() = networkStatus == NetworkStatus.Unavailable || networkStatus == NetworkStatus.Lost
}

sealed interface MainUiEvent : UiEvent {
    data object RetryClicked : MainUiEvent
}

sealed interface MainSideEffect : UiEffect {
    data object NetworkRestored : MainSideEffect
}
