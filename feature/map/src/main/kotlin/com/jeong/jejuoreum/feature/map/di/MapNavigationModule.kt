package com.jeong.jejuoreum.feature.map.di

import com.jeong.jejuoreum.core.navigation.ComposableDestination
import com.jeong.jejuoreum.core.navigation.StartDestination
import com.jeong.jejuoreum.feature.map.navigation.MapDestination
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapNavigationModule {
    @Provides
    @IntoSet
    fun provideMapDestination(): ComposableDestination = MapDestination

    @Provides
    @StartDestination
    @Singleton
    fun provideStartDestination(): String = MapDestination.route
}
