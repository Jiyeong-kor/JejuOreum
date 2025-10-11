package com.jeong.jejuoreum.feature.splash.di

import com.jeong.jejuoreum.core.navigation.SplashNavigation
import com.jeong.jejuoreum.feature.splash.navigation.SplashNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SplashNavigationModule {
    @Binds
    abstract fun bindSplashNavigation(impl: SplashNavigationImpl): SplashNavigation
}
