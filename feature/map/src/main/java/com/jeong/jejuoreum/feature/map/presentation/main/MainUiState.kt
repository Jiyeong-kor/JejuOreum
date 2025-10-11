package com.jeong.jejuoreum.feature.map.presentation.main

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import com.jeong.jejuoreum.core.common.network.NetworkStatus

data class MainUiState(
    val networkStatus: NetworkStatus = NetworkStatus.Unknown,
    val showNetworkDialog: Boolean = false,
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
