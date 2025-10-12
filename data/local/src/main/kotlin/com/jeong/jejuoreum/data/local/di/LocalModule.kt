package com.jeong.jejuoreum.data.local.di

import com.jeong.jejuoreum.data.local.oreum.source.InMemoryOreumLocalDataSource
import com.jeong.jejuoreum.data.local.oreum.source.OreumLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocalModule {
    @Binds
    fun bindOreumLocalDataSource(
        dataSource: InMemoryOreumLocalDataSource
    ): OreumLocalDataSource
}
