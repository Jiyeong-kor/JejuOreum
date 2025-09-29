package com.jeong.jjoreum.presentation.ui.main

import com.jeong.core.utils.network.NetworkStatus

data class MainUiState(
    val networkStatus: NetworkStatus = NetworkStatus.Unknown,
    val showNetworkDialog: Boolean = false,
)

sealed interface MainEvent {
    data object NetworkRestored : MainEvent
}
