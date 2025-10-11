package com.jeong.jejuoreum.feature.profile.di

import com.jeong.jejuoreum.core.navigation.ProfileNavigation
import com.jeong.jejuoreum.feature.profile.navigation.ProfileNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileNavigationModule {
    @Binds
    abstract fun bindProfileNavigation(impl: ProfileNavigationImpl): ProfileNavigation
}
