package com.jeong.jejuoreum.core.ui.di

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.jeong.jejuoreum.core.ui.R
import com.jeong.jejuoreum.core.ui.util.OkHttpNetworkFetcherFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .components {
                add(SvgDecoder.Factory())
                add(OkHttpNetworkFetcherFactory(okHttpClient))
            }
            .error(AppCompatResources.getDrawable(context, R.drawable.error_image)!!)
            .build()
