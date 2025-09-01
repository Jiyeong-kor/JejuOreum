package com.jeong.jjoreum.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.security.ProviderInstaller
import com.jeong.jjoreum.BuildConfig
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.presentation.ui.splash.SplashUiState
import com.jeong.jjoreum.repository.OreumRepository
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs: PreferenceManager,
    private val oreumRepository: OreumRepository,
    private val application: Application
) : ViewModel() {

    val isLoading = MutableStateFlow(true)
    private val _uiState = MutableLiveData<SplashUiState>()
    val uiState: LiveData<SplashUiState> = _uiState

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
            Log.i("SplashViewModel", "✅ Security Provider installed")
        } catch (e: Exception) {
            Log.e("SplashViewModel", "❌ Provider install failed: ${e.message}")
        }
    }

    private fun initKakaoMap() {
        KakaoMapSdk.init(application, BuildConfig.APP_KEY)
    }

    private suspend fun preloadOreumList() {
        oreumRepository.loadOreumListIfNeeded()
            .onFailure { Log.e("SplashViewModel", "❌ Preload failed", it) }
    }

    private suspend fun decideNavigation() {
        val isSignedUp = prefs.isUserRegistered()
        _uiState.value = if (isSignedUp) SplashUiState.GoToMap else SplashUiState.GoToJoin
    }
}
