package com.jeong.jejuoreum.data.oreum.di

import com.jeong.jejuoreum.data.oreum.repository.OreumRepositoryImpl
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import com.jeong.jejuoreum.domain.oreum.usecase.ObserveOreumsUseCase
import com.jeong.jejuoreum.domain.oreum.usecase.RefreshOreumsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface OreumBindsModule {
    @Binds
    fun bindOreumRepository(
        repository: OreumRepositoryImpl
    ): OreumRepository
}

@Module
@InstallIn(SingletonComponent::class)
object OreumProvidesModule {
    @Provides
    @Singleton
    fun provideObserveOreumsUseCase(
        repository: OreumRepository
    ): ObserveOreumsUseCase = ObserveOreumsUseCase(repository)

    @Provides
    @Singleton
    fun provideRefreshOreumsUseCase(
        repository: OreumRepository
    ): RefreshOreumsUseCase = RefreshOreumsUseCase(repository)
}
