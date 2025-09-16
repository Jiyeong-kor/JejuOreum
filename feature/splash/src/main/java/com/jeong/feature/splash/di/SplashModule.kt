package com.jeong.feature.splash.di

import com.jeong.feature.splash.data.SplashInitializerImpl
import com.jeong.feature.splash.data.UserStatusCheckerImpl
import com.jeong.feature.splash.domain.SplashInitializer
import com.jeong.feature.splash.domain.UserStatusChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class SplashModule {
    @Binds
    abstract fun bindUserStatusChecker(
        impl: UserStatusCheckerImpl
    ): UserStatusChecker

    @Binds
    abstract fun bindSplashInitializer(
        impl: SplashInitializerImpl
    ): SplashInitializer
}
