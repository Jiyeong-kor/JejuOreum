package com.jeong.jejuoreum.data.oreum.di

import android.content.Context
import com.jeong.jejuoreum.data.oreum.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideOreumImageBaseUrl(
        @ApplicationContext context: Context,
    ): String =
        context.getString(R.string.oreum_image_base_url).ensureNotBlank(
            property = "jejuOreumBaseUrl"
        )

    @Provides
    @Singleton
    @Named(OREUM_API_BASE_URL)
    fun provideOreumApiBaseUrl(
        @ApplicationContext context: Context,
    ): String =
        context.getString(R.string.oreum_base_url).ensureNotBlank(
            property = "jejuOreumBaseUrl"
        )
}

private fun String.ensureNotBlank(property: String): String {
    require(isNotBlank()) { "Missing $property in local.properties" }
    return this
}
