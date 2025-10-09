package com.jeong.jejuoreum.data.local.di

import android.content.Context
import com.jeong.jejuoreum.data.local.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun providePreferenceManager(
        context: Context
    ): PreferenceManager = PreferenceManager(context)
}
