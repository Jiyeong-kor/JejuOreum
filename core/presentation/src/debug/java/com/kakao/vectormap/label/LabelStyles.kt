package com.kakao.vectormap.label

class LabelStyles private constructor() {
    companion object {
        fun from(@Suppress("UNUSED_PARAMETER") vararg styles: LabelStyle): LabelStyles = LabelStyles()
    }
}
