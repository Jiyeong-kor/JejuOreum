package com.jeong.jejuoreum.feature.detail.di

import com.jeong.jejuoreum.feature.detail.presentation.detail.AndroidDetailMessageProvider
import com.jeong.jejuoreum.feature.detail.presentation.detail.DetailMessageProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DetailPresentationModule {

    @Binds
    abstract fun bindDetailMessageProvider(
        impl: AndroidDetailMessageProvider,
    ): DetailMessageProvider
