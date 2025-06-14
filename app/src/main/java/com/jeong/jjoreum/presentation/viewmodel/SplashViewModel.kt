package com.jeong.jjoreum.presentation.viewmodel

import android.content.Context
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val prefs: PreferenceManager,
    private val oreumRepository: OreumRepository,
    private val context: Context
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
            ProviderInstaller.installIfNeeded(context)
            Log.i("SplashViewModel", "✅ Security Provider installed")
        } catch (e: Exception) {
            Log.e("SplashViewModel", "❌ Provider install failed: ${e.message}")
        }
    }

    private fun initKakaoMap() {
        KakaoMapSdk.init(context, BuildConfig.APP_KEY)
    }

    private suspend fun preloadOreumList() {
        oreumRepository.loadOreumListIfNeeded()
    }

    private fun decideNavigation() {
        val isSignedUp = prefs.isUserRegistered()
        _uiState.value = if (isSignedUp) SplashUiState.GoToMap else SplashUiState.GoToJoin
    }
}
