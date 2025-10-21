package com.jeong.jejuoreum.data.oreum.di

import com.jeong.jejuoreum.data.oreum.remote.api.OreumRetrofitInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal const val OREUM_OKHTTP = "oreumOkHttp"
internal const val OREUM_RETROFIT = "oreumRetrofit"

@Module
@InstallIn(SingletonComponent::class)
object OreumNetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    @Named(OREUM_OKHTTP)
    fun provideOreumOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @Named(OREUM_RETROFIT)
    fun provideOreumRetrofit(
        @Named(OREUM_API_BASE_URL) baseUrl: String,
        @Named(OREUM_OKHTTP) okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideOreumApi(
        @Named(OREUM_RETROFIT) retrofit: Retrofit,
    ): OreumRetrofitInterface =
        retrofit.create(OreumRetrofitInterface::class.java)
}
