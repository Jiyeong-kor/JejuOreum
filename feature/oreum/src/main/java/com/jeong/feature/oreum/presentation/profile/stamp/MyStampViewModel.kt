package com.jeong.feature.oreum.presentation.profile.stamp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.feature.oreum.domain.usecase.GetCurrentUserNicknameUseCase
import com.jeong.feature.oreum.domain.usecase.LoadStampedOreumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyStampViewModel @Inject constructor(
    private val loadStampedOreumsUseCase: LoadStampedOreumsUseCase,
    private val getCurrentUserNicknameUseCase: GetCurrentUserNicknameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyStampUiState())
    val uiState: StateFlow<MyStampUiState> = _uiState.asStateFlow()

    init {
        refreshNickname()
    }

    fun loadStampedList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            loadStampedOreumsUseCase()
                .onSuccess { stamps ->
                    _uiState.update {
                        it.copy(
                            stampedList = stamps,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    private fun refreshNickname() {
        viewModelScope.launch {
            getCurrentUserNicknameUseCase()
                .onSuccess { nickname ->
                    _uiState.update { it.copy(nickname = nickname) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
