package com.jeong.jejuoreum.data.remote.di

import android.content.Context
import android.net.ConnectivityManager
import com.jeong.jejuoreum.core.common.network.NetworkMonitor
import com.jeong.jejuoreum.data.remote.connectivity.DefaultConnectivityRepository
import com.jeong.jejuoreum.data.remote.network.DefaultNetworkMonitor
import com.jeong.jejuoreum.domain.oreum.repository.ConnectivityRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ConnectivityModule {

    @Binds
    @Singleton
    fun bindsConnectivityRepository(
        impl: DefaultConnectivityRepository
    ): ConnectivityRepository

    @Binds
    @Singleton
    fun bindsNetworkMonitor(
        impl: DefaultNetworkMonitor
    ): NetworkMonitor

    companion object {
        @Provides
        @Singleton
        fun provideConnectivityManager(
            @ApplicationContext context: Context
        ): ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}
