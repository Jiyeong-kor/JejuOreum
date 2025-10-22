package com.kakao.vectormap

import android.content.Context
import android.view.View

class MapView(context: Context) : View(context) {
    var isFinishManually: Boolean = false

    fun start(
        @Suppress("UNUSED_PARAMETER") lifeCycleCallback: MapLifeCycleCallback,
        readyCallback: KakaoMapReadyCallback,
    ) {
        readyCallback.onMapReady(KakaoMap())
    }

    fun resume() {
        // No-op stub implementation
    }

    fun pause() {
        // No-op stub implementation
    }

    fun finish() {
        // No-op stub implementation
    }
}

open class MapLifeCycleCallback {
    open fun onMapDestroy() {
        // No-op stub implementation
    }

    open fun onMapError(@Suppress("UNUSED_PARAMETER") error: Exception) {
        // No-op stub implementation
    }
}

abstract class KakaoMapReadyCallback {
    open fun onMapReady(@Suppress("UNUSED_PARAMETER") map: KakaoMap) {
        // No-op stub implementation
    }
}
