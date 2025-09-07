package com.jeong.jjoreum.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.security.ProviderInstaller
import com.jeong.domain.repository.OreumRepository
import com.jeong.jjoreum.BuildConfig
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.presentation.ui.splash.SplashUiState
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
    private val prefs: PreferenceManager,
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
            Timber.i("✅ Security Provider installed")
        } catch (e: Exception) {
            Timber.e(e, "❌ Provider install failed: %s", e.message)
        }
    }

    private fun initKakaoMap() {
        KakaoMapSdk.init(application, BuildConfig.APP_KEY)
    }

    private suspend fun preloadOreumList() {
        oreumRepository.loadOreumListIfNeeded()
            .onFailure { Timber.e(it, "❌ Preload failed") }
    }

    private suspend fun decideNavigation() {
        val isSignedUp = prefs.isUserRegistered()
        _uiState.value = if (isSignedUp) SplashUiState.GoToMap else SplashUiState.GoToJoin
    }
}
