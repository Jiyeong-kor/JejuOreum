package com.jeong.jjoreum

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import timber.log.Timber

@HiltAndroidApp
class JJOreumApplication : Application(), SingletonImageLoader.Factory {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ImageLoaderEntryPoint {
        fun imageLoader(): ImageLoader
    }

    override fun newImageLoader(context: Context): ImageLoader {
        val ep = EntryPointAccessors.fromApplication(
            this, ImageLoaderEntryPoint::class.java
        )
        return ep.imageLoader()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
