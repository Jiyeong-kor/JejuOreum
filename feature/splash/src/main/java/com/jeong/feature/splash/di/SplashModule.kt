package com.jeong.feature.splash.di

import com.jeong.feature.splash.data.UserStatusCheckerImpl
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
}
