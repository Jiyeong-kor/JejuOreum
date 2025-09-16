package com.jeong.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.domain.repository.OreumRepository
import com.jeong.feature.splash.domain.SplashInitializer
import com.jeong.feature.splash.domain.UserStatusChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashInitializer: SplashInitializer,
    private val userStatusChecker: UserStatusChecker,
    private val oreumRepository: OreumRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _uiState = MutableStateFlow<SplashUiState?>(null)
    val uiState: StateFlow<SplashUiState?> = _uiState.asStateFlow()

    fun checkUserStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            splashInitializer.initialize()
            preloadOreumList()
            decideNavigation()
            _isLoading.value = false
        }
    }

    private suspend fun preloadOreumList() {
        oreumRepository.loadOreumListIfNeeded()
            .onFailure { Timber.Forest.e(it, "❌ Preload failed") }
    }

    private suspend fun decideNavigation() {
        val isSignedUp = userStatusChecker.isUserRegistered()
        _uiState.value = if (isSignedUp) SplashUiState.GoToMap else SplashUiState.GoToJoin
    }
}
