package com.jeong.jejuoreum.data.remote.di

import com.jeong.jejuoreum.data.remote.oreum.source.FakeOreumRemoteDataSource
import com.jeong.jejuoreum.data.remote.oreum.source.OreumRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteModule {
    @Binds
    fun bindOreumRemoteDataSource(
        dataSource: FakeOreumRemoteDataSource
    ): OreumRemoteDataSource
}
