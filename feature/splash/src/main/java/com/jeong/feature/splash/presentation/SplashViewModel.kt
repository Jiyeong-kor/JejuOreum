package com.jeong.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.feature.splash.domain.model.SplashDestination
import com.jeong.feature.splash.domain.usecase.PrepareSplashUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prepareSplashUseCase: PrepareSplashUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun initialize() {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(
                    isLoading = true,
                    destination = null,
                    errorMessage = null,
                )
            }

            val result = prepareSplashUseCase()
            _uiState.update { current ->
                result.fold(
                    onSuccess = { destination: SplashDestination ->
                        current.copy(
                            isLoading = false,
                            destination = destination,
                        )
                    },
                    onFailure = { throwable ->
                        Timber.e(throwable, "❌ Splash 준비 실패")
                        current.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: DEFAULT_ERROR_MESSAGE,
                        )
                    }
                )
            }
        }
    }

    fun consumeError() {
        _uiState.update { current -> current.copy(errorMessage = null) }
    }

    companion object {
        private const val DEFAULT_ERROR_MESSAGE = "오류가 발생하였습니다."
    }
}
