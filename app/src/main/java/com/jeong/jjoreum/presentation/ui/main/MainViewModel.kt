package com.jeong.jjoreum.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.core.utils.network.NetworkMonitor
import com.jeong.core.utils.network.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MainEvent>()
    val events: SharedFlow<MainEvent> = _events.asSharedFlow()

    init {
        observeNetwork()
    }

    fun onRetryClicked() {
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
        val shouldNotifyRestored = !showDialog && (_uiState.value.showNetworkDialog || fromUser)

        _uiState.update { current ->
            current.copy(
                networkStatus = status,
                showNetworkDialog = showDialog,
            )
        }

        if (shouldNotifyRestored) {
            viewModelScope.launch { _events.emit(MainEvent.NetworkRestored) }
        }
    }

    private fun shouldShowDialog(status: NetworkStatus): Boolean = when (status) {
        NetworkStatus.Available, NetworkStatus.Losing, NetworkStatus.Unknown -> false
        NetworkStatus.Lost, NetworkStatus.Unavailable -> true
    }
}
