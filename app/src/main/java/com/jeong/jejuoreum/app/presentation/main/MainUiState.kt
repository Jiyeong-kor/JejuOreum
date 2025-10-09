package com.jeong.jejuoreum.feature.main.presentation

import com.jeong.jejuoreum.core.ui.state.UiEffect
import com.jeong.jejuoreum.core.ui.state.UiEvent
import com.jeong.jejuoreum.core.ui.state.UiState
import com.jeong.jejuoreum.core.utils.network.NetworkStatus

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
