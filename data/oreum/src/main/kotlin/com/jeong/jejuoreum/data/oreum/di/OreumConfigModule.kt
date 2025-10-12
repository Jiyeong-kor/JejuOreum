package com.jeong.jejuoreum.data.oreum.di

import com.jeong.jejuoreum.data.oreum.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

internal const val OREUM_IMAGE_BASE_URL = "oreumImageBaseUrl"
internal const val OREUM_API_BASE_URL = "oreumApiBaseUrl"

@Module
@InstallIn(SingletonComponent::class)
object OreumConfigModule {

    @Provides
    @Singleton
    @Named(OREUM_IMAGE_BASE_URL)
    fun provideOreumImageBaseUrl(): String =
        BuildConfig.OREUM_IMAGE_BASE_URL.ensureNotBlank(
            property = "jejuOreumImageBaseUrl"
        )

    @Provides
    @Singleton
    @Named(OREUM_API_BASE_URL)
    fun provideOreumApiBaseUrl(): String =
        BuildConfig.OREUM_BASE_URL.ensureNotBlank(
            property = "jejuOreumBaseUrl"
        )
}

private fun String.ensureNotBlank(property: String): String {
    require(isNotBlank()) { "Missing $property in local.properties" }
