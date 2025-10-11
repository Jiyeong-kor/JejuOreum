package com.jeong.jejuoreum.core.common

import androidx.annotation.StringRes

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    data class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText()
}
