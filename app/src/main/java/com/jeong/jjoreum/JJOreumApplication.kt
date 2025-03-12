package com.jeong.jjoreum

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.kakao.vectormap.KakaoMapSdk

/**
 *  애플리케이션 전역에서 사용하는 Application 클래스
 */
class JJOreumApplication : Application() {
    companion object {
        private lateinit var jjOreumApplication: JJOreumApplication

        /**
         * Application 인스턴스를 반환
         */
        fun getInstance(): JJOreumApplication = jjOreumApplication
    }

    override fun onCreate() {
        super.onCreate()
        jjOreumApplication = this

        // 카카오 맵 SDK 초기화
        KakaoMapSdk.init(this, "d1617d031980d89d9587daeb98c120ff")

        // 화면을 세로 모드로 고정
        settingScreenPortrait()
    }

    /**
     *  모든 Activity의 화면을 세로 모드로 고정
     */
    private fun settingScreenPortrait() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                // 각 Activity가 생성될 때 화면을 세로 모드로 설정
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
