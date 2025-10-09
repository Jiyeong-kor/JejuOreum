package com.jeong.jejuoreum.feature.main.di

import com.jeong.jejuoreum.feature.main.data.repository.DefaultConnectivityRepository
import com.jeong.jejuoreum.feature.main.domain.repository.ConnectivityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MainNetworkModule {

    @Binds
    abstract fun bindConnectivityRepository(
        repository: DefaultConnectivityRepository
    ): ConnectivityRepository
}
