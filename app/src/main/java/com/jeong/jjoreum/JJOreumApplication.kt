package com.jeong.jjoreum

import com.jeong.jjoreum.data.model.api.RetrofitOkHttpManager
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import coil.ImageLoader
import coil.ImageLoaderFactory

/**
 *  애플리케이션 전역에서 사용하는 Application 클래스
 */
class JJOreumApplication : Application(), ImageLoaderFactory {
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

    override fun newImageLoader(): ImageLoader {
        val client = RetrofitOkHttpManager.getUnsafeOkHttpClient()
        return ImageLoader.Builder(this)
            .okHttpClient(client)
            .build()
    }

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