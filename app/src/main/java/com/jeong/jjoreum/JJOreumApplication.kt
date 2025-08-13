package com.jeong.jjoreum

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class JJOreumApplication : Application(), ImageLoaderFactory {
    @Inject
    lateinit var imageLoader: ImageLoader

    companion object {
        private lateinit var jjOreumApplication: JJOreumApplication

        fun getInstance(): JJOreumApplication = jjOreumApplication
    }

    override fun onCreate() {
        super.onCreate()
        jjOreumApplication = this

        // 화면을 세로 모드로 고정
        settingScreenPortrait()
    }

    override fun newImageLoader(): ImageLoader = imageLoader

    private fun settingScreenPortrait() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            override fun onActivityStarted(p0: Activity) {}
            override fun onActivityResumed(p0: Activity) {}
            override fun onActivityPaused(p0: Activity) {}
            override fun onActivityStopped(p0: Activity) {}
            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
            override fun onActivityDestroyed(p0: Activity) {}
        })
    }
}