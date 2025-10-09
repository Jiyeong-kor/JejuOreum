package com.jeong.jejuoreum.feature.map.presentation.main

import com.jeong.jejuoreum.data.remote.NetworkStatus

data class MainUiState(
    val networkStatus: NetworkStatus = NetworkStatus.Unknown,
    val showNetworkDialog: Boolean = false
) {
    val isOffline: Boolean
        get() = networkStatus == NetworkStatus.Unavailable || networkStatus == NetworkStatus.Lost
}

sealed interface MainUiEvent {
    data object RetryClicked : MainUiEvent
}

sealed interface MainSideEffect {
    data object NetworkRestored : MainSideEffect
}
