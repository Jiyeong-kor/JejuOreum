package com.jeong.jejuoreum.feature.map.di

import com.jeong.jejuoreum.core.navigation.MapNavigation
import com.jeong.jejuoreum.feature.map.navigation.MapNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MapNavigationModule {
    @Binds
    abstract fun bindMapNavigation(impl: MapNavigationImpl): MapNavigation
}
