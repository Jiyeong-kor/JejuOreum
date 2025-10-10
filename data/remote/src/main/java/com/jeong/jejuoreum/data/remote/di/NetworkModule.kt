package com.jeong.jejuoreum.data.remote.di

import com.jeong.jejuoreum.data.remote.oreum.api.OreumRetrofitInterface
import com.jeong.jejuoreum.data.remote.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(resolveBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideOreumApi(retrofit: Retrofit): OreumRetrofitInterface =
        retrofit.create(OreumRetrofitInterface::class.java)

    private fun resolveBaseUrl(): String {
        val baseUrl = BuildConfig.JEJU_OREUM_URL
        require(baseUrl.isNotBlank()) { "JEJU_OREUM_URL must be defined in local.properties" }
        return baseUrl
    }
}
