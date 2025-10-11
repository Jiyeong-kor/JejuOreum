package com.jeong.jejuoreum.core.navigation

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NavigationProvider {
    fun detailNavigation(): DetailNavigation
    fun mapNavigation(): MapNavigation
    fun onboardingNavigation(): OnboardingNavigation
    fun profileNavigation(): ProfileNavigation
    fun splashNavigation(): SplashNavigation
}
