package com.jeong.jejuoreum.data.oreum.di

import com.jeong.jejuoreum.data.oreum.datasource.local.InMemoryOreumLocalDataSource
import com.jeong.jejuoreum.data.oreum.datasource.local.OreumLocalDataSource
import com.jeong.jejuoreum.data.oreum.repository.StampRepositoryImpl
import com.jeong.jejuoreum.data.remote.oreum.datasource.OreumRemoteDataSource
import com.jeong.jejuoreum.data.remote.oreum.datasource.StubOreumRemoteDataSource
import com.jeong.jejuoreum.data.oreum.repository.OreumRepositoryImpl
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import com.jeong.jejuoreum.domain.oreum.repository.StampRepository
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

    @Binds
    fun bindsStampRepository(impl: StampRepositoryImpl): StampRepository
}
