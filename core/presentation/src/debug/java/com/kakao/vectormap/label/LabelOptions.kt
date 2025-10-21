package com.kakao.vectormap.label

import com.kakao.vectormap.LatLng

class LabelOptions private constructor() {
    fun setTexts(@Suppress("UNUSED_PARAMETER") builder: LabelTextBuilder): LabelOptions = this
    fun setStyles(@Suppress("UNUSED_PARAMETER") styles: LabelStyles): LabelOptions = this
    fun setTag(@Suppress("UNUSED_PARAMETER") tag: String): LabelOptions = this

    companion object {
        fun from(@Suppress("UNUSED_PARAMETER") latLng: LatLng): LabelOptions = LabelOptions()
    }
}
