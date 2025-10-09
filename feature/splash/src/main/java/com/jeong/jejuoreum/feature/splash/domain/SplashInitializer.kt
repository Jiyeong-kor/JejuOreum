package com.jeong.jejuoreum.feature.splash.domain

import android.content.Context
import com.google.android.gms.security.ProviderInstaller
import com.jeong.jejuoreum.feature.splash.BuildConfig
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class SplashInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun initialize(): Result<Unit> {
        return runCatching {
            ProviderInstaller.installIfNeeded(context)
            KakaoMapSdk.init(context, BuildConfig.APP_KEY)
            Timber.Forest.i("✅ Splash dependencies initialized")
        }.onFailure { throwable ->
            Timber.Forest.e(
                throwable,
                "❌ Splash initialization failed: %s",
                throwable.message
            )
        }
    }
}
