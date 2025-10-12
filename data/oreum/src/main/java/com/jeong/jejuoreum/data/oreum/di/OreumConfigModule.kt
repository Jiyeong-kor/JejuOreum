package com.jeong.jejuoreum.data.oreum.di

import com.jeong.jejuoreum.data.oreum.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

internal const val OREUM_IMAGE_BASE_URL = "oreumImageBaseUrl"

@Module
@InstallIn(SingletonComponent::class)
object OreumConfigModule {

    @Provides
    @Singleton
    @Named(OREUM_IMAGE_BASE_URL)
    fun provideOreumImageBaseUrl(): String {
        val value = BuildConfig.OREUM_IMAGE_BASE_URL
        require(value.isNotBlank()) {
            "Missing jejuOreumImageBaseUrl in local.properties"
        }
        return value
    }
}
