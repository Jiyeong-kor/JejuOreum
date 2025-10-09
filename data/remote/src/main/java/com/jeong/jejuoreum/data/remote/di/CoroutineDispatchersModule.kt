package com.jeong.jejuoreum.data.remote.di

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.core.common.coroutines.DefaultCoroutineDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatchersModule {

    @Provides
    @Singleton
    fun providesCoroutineDispatcherProvider(): CoroutineDispatcherProvider =
        DefaultCoroutineDispatcherProvider()
}
