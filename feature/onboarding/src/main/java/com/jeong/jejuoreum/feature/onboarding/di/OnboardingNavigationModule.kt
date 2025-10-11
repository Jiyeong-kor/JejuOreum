package com.jeong.jejuoreum.feature.onboarding.di

import com.jeong.jejuoreum.core.navigation.OnboardingNavigation
import com.jeong.jejuoreum.feature.onboarding.navigation.OnboardingNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingNavigationModule {
    @Binds
    abstract fun bindOnboardingNavigation(impl: OnboardingNavigationImpl): OnboardingNavigation
}
