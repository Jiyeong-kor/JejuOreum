package com.kakao.vectormap.label

class LabelStyle private constructor() {
    fun setTextStyles(@Suppress("UNUSED_PARAMETER") vararg styles: LabelTextStyle): LabelStyle = this

    companion object {
        fun from(@Suppress("UNUSED_PARAMETER") drawableResId: Int): LabelStyle = LabelStyle()
    }
}
