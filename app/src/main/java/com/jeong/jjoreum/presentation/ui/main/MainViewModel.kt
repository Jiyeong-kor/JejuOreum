package com.jeong.jjoreum.presentation.ui.main

import androidx.lifecycle.viewModelScope
import com.jeong.core.ui.viewmodel.BaseViewModel
import com.jeong.core.utils.network.NetworkMonitor
import com.jeong.core.utils.network.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor
) : BaseViewModel<MainUiEvent, MainSideEffect, MainUiState>(MainUiState()) {

    init {
        observeNetwork()
    }

    override fun handleEvent(event: MainUiEvent) {
        when (event) {
            MainUiEvent.RetryClicked -> onRetry()
        }
    }

    private fun onRetry() {
        viewModelScope.launch {
            val status = networkMonitor.getCurrentStatus()
            applyStatus(status, fromUser = true)
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.observe().collectLatest { status ->
                applyStatus(status)
            }
        }
        viewModelScope.launch {
            val status = networkMonitor.getCurrentStatus()
            applyStatus(status)
        }
    }

    private fun applyStatus(status: NetworkStatus, fromUser: Boolean = false) {
        val showDialog = shouldShowDialog(status)
        val shouldNotifyRestored = !showDialog && (state.value.showNetworkDialog || fromUser)

        setState {
            copy(
                networkStatus = status,
                showNetworkDialog = showDialog,
            )
        }

        if (shouldNotifyRestored) {
            sendEffect { MainSideEffect.NetworkRestored }
        }
    }

    private fun shouldShowDialog(status: NetworkStatus): Boolean = when (status) {
        NetworkStatus.Available, NetworkStatus.Losing, NetworkStatus.Unknown -> false
        NetworkStatus.Lost, NetworkStatus.Unavailable -> true
    }
}
