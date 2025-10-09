package com.jeong.jejuoreum.feature.splash.di

import com.jeong.jejuoreum.feature.splash.data.SplashInitializerImpl
import com.jeong.jejuoreum.feature.splash.data.UserStatusCheckerImpl
import com.jeong.jejuoreum.feature.splash.domain.SplashInitializer
import com.jeong.jejuoreum.feature.splash.domain.UserStatusChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SplashModule {
    @Binds
    @Singleton
    abstract fun bindUserStatusChecker(
        impl: UserStatusCheckerImpl
    ): UserStatusChecker

    @Binds
    @Singleton
    abstract fun bindSplashInitializer(
        impl: SplashInitializerImpl
    ): SplashInitializer
}
