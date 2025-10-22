package com.jeong.jejuoreum.feature.profile.presentation.stamp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jejuoreum.core.architecture.UiText
import com.jeong.jejuoreum.domain.oreum.usecase.LoadStampedOreumsUseCase
import com.jeong.jejuoreum.domain.user.usecase.GetCurrentUserNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import com.jeong.jejuoreum.feature.profile.R

@HiltViewModel
class MyStampViewModel @Inject constructor(
    private val loadStampedOreumsUseCase: LoadStampedOreumsUseCase,
    private val getCurrentUserNicknameUseCase: GetCurrentUserNicknameUseCase,
    @Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyStampUiState())
    val uiState: StateFlow<MyStampUiState> = _uiState.asStateFlow()

    fun loadStampedList() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val nicknameResult = getCurrentUserNicknameUseCase()
            val stampsResult = loadStampedOreumsUseCase()

            val nickname = nicknameResult.getOrNull().orEmpty()
            val stampedList = stampsResult.getOrDefault(emptyList())
            val error = nicknameResult.exceptionOrNull() ?: stampsResult.exceptionOrNull()

            if (error != null) {
                Timber.w(error, "Failed to load stamped oreums")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nickname = nickname,
                        stampedList = stampedList,
                        errorMessage = UiText.StringResource(R.string.oreum_stamp_load_error),
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nickname = nickname,
                        stampedList = stampedList,
                        errorMessage = null,
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
