package com.jeong.jejuoreum.data.oreum.di

import com.jeong.jejuoreum.data.oreum.local.source.InMemoryOreumLocalDataSource
import com.jeong.jejuoreum.data.oreum.local.source.OreumLocalDataSource
import com.jeong.jejuoreum.data.oreum.remote.source.NetworkOreumRemoteDataSource
import com.jeong.jejuoreum.data.oreum.remote.source.OreumRemoteDataSource
import com.jeong.jejuoreum.data.oreum.repository.StampRepositoryImpl
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
    fun bindsOreumRemoteDataSource(impl: NetworkOreumRemoteDataSource): OreumRemoteDataSource

    @Binds
    fun bindsOreumLocalDataSource(impl: InMemoryOreumLocalDataSource): OreumLocalDataSource

    @Binds
    fun bindsStampRepository(impl: StampRepositoryImpl): StampRepository
}
