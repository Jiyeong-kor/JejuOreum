package com.jeong.feature.splash.presentation

import androidx.lifecycle.viewModelScope
import com.jeong.core.ui.viewmodel.BaseViewModel
import com.jeong.feature.splash.domain.usecase.PrepareSplashUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prepareSplashUseCase: PrepareSplashUseCase
) : BaseViewModel<SplashUiEvent, SplashSideEffect, SplashUiState>(SplashUiState()) {

    override fun handleEvent(event: SplashUiEvent) {
        when (event) {
            SplashUiEvent.Initialize -> initialize()
            SplashUiEvent.ErrorConsumed -> clearError()
        }
    }

    private fun initialize() {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }

            val result = runCatching { prepareSplashUseCase() }
            result.fold(
                onSuccess = { destination ->
                    setState { copy(isLoading = false) }
                    sendEffect { SplashSideEffect.NavigateTo(destination) }
                },
                onFailure = { throwable ->
                    Timber.e(throwable, "❌ Splash 준비 실패")
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: DEFAULT_ERROR_MESSAGE,
                        )
                    }
                })
        }
    }

    private fun clearError() {
        setState { copy(errorMessage = null) }
    }

    companion object {
        private const val DEFAULT_ERROR_MESSAGE = "오류가 발생하였습니다."
    }
}
