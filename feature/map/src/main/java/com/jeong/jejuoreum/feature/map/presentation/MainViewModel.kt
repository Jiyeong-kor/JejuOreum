package com.jeong.jejuoreum.feature.map.presentation

import com.jeong.jejuoreum.core.common.network.NetworkStatus
import com.jeong.jejuoreum.core.presentation.CommonBaseViewModel
import com.jeong.jejuoreum.domain.oreum.usecase.GetCurrentConnectivityStatusUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveConnectivityStatusUseCase
import com.jeong.jejuoreum.feature.map.presentation.main.MainSideEffect
import com.jeong.jejuoreum.feature.map.presentation.main.MainUiEvent
import com.jeong.jejuoreum.feature.map.presentation.main.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeConnectivityStatusUseCase: ObserveConnectivityStatusUseCase,
    private val getCurrentConnectivityStatusUseCase: GetCurrentConnectivityStatusUseCase,
) : CommonBaseViewModel<MainUiState, MainUiEvent, MainSideEffect>() {

    init {
        observeNetwork()
    }

    override fun initialState(): MainUiState = MainUiState()

    override fun handleEvent(event: MainUiEvent) {
        when (event) {
            MainUiEvent.RetryClicked -> onRetry()
        }
    }

    private fun onRetry() {
        launch {
            val status = getCurrentConnectivityStatusUseCase()
            applyStatus(status, fromUser = true)
        }
    }

    private fun observeNetwork() {
        launch {
            observeConnectivityStatusUseCase().collectLatest { status ->
                applyStatus(status)
            }
        }
        launch {
            val status = getCurrentConnectivityStatusUseCase()
            applyStatus(status)
        }
    }

    private fun applyStatus(status: NetworkStatus, fromUser: Boolean = false) {
        val shouldShowDialog = when (status) {
            NetworkStatus.Available, NetworkStatus.Losing, NetworkStatus.Unknown -> false
            NetworkStatus.Lost, NetworkStatus.Unavailable -> true
        }
        val notifyRestored = !shouldShowDialog && (currentState.showNetworkDialog || fromUser)

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
