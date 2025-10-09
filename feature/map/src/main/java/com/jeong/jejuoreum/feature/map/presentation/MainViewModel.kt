package com.jeong.jejuoreum.feature.map.presentation

import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.feature.map.presentation.main.MainSideEffect
import com.jeong.jejuoreum.feature.map.presentation.main.MainUiEvent
import com.jeong.jejuoreum.feature.map.presentation.main.MainUiState
import com.jeong.jejuoreum.feature.map.presentation.base.BaseViewModel
import com.jeong.jejuoreum.data.remote.NetworkStatus
import com.jeong.jejuoreum.feature.map.domain.usecase.GetCurrentConnectivityStatusUseCase
import com.jeong.jejuoreum.feature.map.domain.usecase.ObserveConnectivityStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeConnectivityStatusUseCase: ObserveConnectivityStatusUseCase,
    private val getCurrentConnectivityStatusUseCase: GetCurrentConnectivityStatusUseCase
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
            val status = getCurrentConnectivityStatusUseCase()
            applyStatus(status, fromUser = true)
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            observeConnectivityStatusUseCase().collectLatest { status ->
                applyStatus(status)
            }
        }
        viewModelScope.launch {
            val status = getCurrentConnectivityStatusUseCase()
            applyStatus(status)
        }
    }

    private fun applyStatus(status: NetworkStatus, fromUser: Boolean = false) {
        val shouldShowDialog = when (status) {
            NetworkStatus.Available, NetworkStatus.Losing, NetworkStatus.Unknown -> false
            NetworkStatus.Lost, NetworkStatus.Unavailable -> true
        }
        val notifyRestored = !shouldShowDialog && (state.value.showNetworkDialog || fromUser)

        setState {
            copy(
                networkStatus = status,
                showNetworkDialog = shouldShowDialog
            )
        }

        if (notifyRestored) {
            sendEffect { MainSideEffect.NetworkRestored }
        }
    }
}
