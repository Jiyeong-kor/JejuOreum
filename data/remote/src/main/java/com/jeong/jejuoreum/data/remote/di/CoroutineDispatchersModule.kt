package com.jeong.jejuoreum.data.remote.di

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.core.common.coroutines.DefaultCoroutineDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatchersModule {

    @Provides
    @Singleton
    fun providesCoroutineDispatcherProvider(
        @Named("ioDispatcher") ioDispatcher: CoroutineDispatcher,
        @Named("defaultDispatcher") defaultDispatcher: CoroutineDispatcher,
        @Named("mainDispatcher") mainDispatcher: CoroutineDispatcher,
    ): CoroutineDispatcherProvider =
        DefaultCoroutineDispatcherProvider(
            io = ioDispatcher,
            computation = defaultDispatcher,
            main = mainDispatcher,
        )

    @Provides
    @Named("ioDispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Named("defaultDispatcher")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Named("mainDispatcher")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
