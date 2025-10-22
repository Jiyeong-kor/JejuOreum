package com.kakao.vectormap.label

class LabelLayer {
    var isVisible: Boolean = false
    var isClickable: Boolean = false

    fun addLabel(@Suppress("UNUSED_PARAMETER") options: LabelOptions): Label = Label()

    fun remove(@Suppress("UNUSED_PARAMETER") label: Label) {
        // No-op stub implementation
    }

    fun removeAll() {
        // No-op stub implementation
    }
}

class LabelManager {
    fun addLayer(@Suppress("UNUSED_PARAMETER") options: LabelLayerOptions): LabelLayer = LabelLayer()
}
