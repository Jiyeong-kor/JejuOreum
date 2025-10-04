package com.jeong.data.di

import com.jeong.data.datasource.local.InMemoryOreumLocalDataSource
import com.jeong.data.datasource.local.OreumLocalDataSource
import com.jeong.data.datasource.remote.OreumRemoteDataSource
import com.jeong.data.datasource.remote.StubOreumRemoteDataSource
import com.jeong.data.repository.OreumRepositoryImpl
import com.jeong.domain.repository.OreumRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface OreumDataModule {

    @Binds
    fun bindsOreumRepository(impl: OreumRepositoryImpl): OreumRepository

    @Binds
    fun bindsOreumRemoteDataSource(impl: StubOreumRemoteDataSource): OreumRemoteDataSource

    @Binds
    fun bindsOreumLocalDataSource(impl: InMemoryOreumLocalDataSource): OreumLocalDataSource
}
