package com.kakao.vectormap

import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.label.LabelManager

class KakaoMap {
    val labelManager: LabelManager? = LabelManager()
    val viewport: Viewport = Viewport()
    var zoomLevel: Int = 0

    fun moveCamera(@Suppress("UNUSED_PARAMETER") update: CameraUpdate) {
        // No-op stub implementation
    }

    fun setOnPoiClickListener(@Suppress("UNUSED_PARAMETER") listener: ((Any?, LatLng, Any?, Any?) -> Unit)?) {
        // No-op stub implementation
    }

    fun setOnViewportClickListener(@Suppress("UNUSED_PARAMETER") listener: ((Any?, Any?, Any?) -> Unit)?) {
        // No-op stub implementation
    }

    fun setOnCameraMoveEndListener(@Suppress("UNUSED_PARAMETER") listener: ((KakaoMap, Any?, Any?) -> Unit)?) {
        // No-op stub implementation
    }

    fun fromScreenPoint(@Suppress("UNUSED_PARAMETER") x: Int, @Suppress("UNUSED_PARAMETER") y: Int): LatLng? {
        return LatLng.from(0.0, 0.0)
    }
}

data class Viewport(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
)
