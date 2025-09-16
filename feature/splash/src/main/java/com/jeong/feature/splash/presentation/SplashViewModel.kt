package com.jeong.feature.splash.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.security.ProviderInstaller
import com.jeong.domain.repository.OreumRepository
import com.jeong.feature.splash.BuildConfig
import com.jeong.feature.splash.domain.UserStatusChecker
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userStatusChecker: UserStatusChecker,
    private val oreumRepository: OreumRepository,
    private val application: Application
) : ViewModel() {

    val isLoading = MutableStateFlow(true)
    private val _uiState = MutableStateFlow<SplashUiState?>(null)
    val uiState: StateFlow<SplashUiState?> = _uiState.asStateFlow()

    fun checkUserStatus() {
        viewModelScope.launch {
            installSecurityProvider()
            initKakaoMap()
            preloadOreumList()
            decideNavigation()
            isLoading.value = false
        }
    }

    private fun installSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(application)
            Timber.Forest.i("✅ Security Provider installed")
        } catch (e: Exception) {
            Timber.Forest.e(e, "❌ Provider install failed: %s", e.message)
        }
    }

    private fun initKakaoMap() {
        KakaoMapSdk.init(application, BuildConfig.APP_KEY)
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
