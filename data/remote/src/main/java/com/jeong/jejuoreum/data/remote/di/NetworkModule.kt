package com.jeong.jejuoreum.data.remote.di

import android.content.Context
import com.jeong.jejuoreum.data.remote.api.OreumRetrofitInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://example.com/") // 실제 API base URL로 교체
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideOreumApi(retrofit: Retrofit): OreumRetrofitInterface =
        retrofit.create(OreumRetrofitInterface::class.java)
}
