package com.kakao.vectormap.label

class LabelTextStyle private constructor() {
    companion object {
        fun from(
            @Suppress("UNUSED_PARAMETER") textSize: Int,
            @Suppress("UNUSED_PARAMETER") textColor: Int,
            @Suppress("UNUSED_PARAMETER") strokeWidth: Int,
            @Suppress("UNUSED_PARAMETER") strokeColor: Int,
        ): LabelTextStyle = LabelTextStyle()
    }
}
