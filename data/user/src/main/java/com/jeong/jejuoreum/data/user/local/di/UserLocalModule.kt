package com.jeong.jejuoreum.data.user.local.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.jeong.jejuoreum.data.user.local.PreferenceManager
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
        @ApplicationContext context: Context
    ): PreferenceManager = PreferenceManager(context)
}
