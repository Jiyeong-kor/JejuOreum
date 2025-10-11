package com.jeong.jejuoreum.feature.detail.di

import com.jeong.jejuoreum.core.navigation.DetailNavigation
import com.jeong.jejuoreum.feature.detail.navigation.DetailNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DetailNavigationModule {
    @Binds
    abstract fun bindDetailNavigation(impl: DetailNavigationImpl): DetailNavigation
}
