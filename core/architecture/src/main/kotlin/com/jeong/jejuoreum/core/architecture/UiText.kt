package com.jeong.jejuoreum.core.architecture

import androidx.annotation.StringRes

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    data class StringResource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList(),
    ) : UiText() {
        constructor(@StringRes resId: Int, vararg args: Any) : this(resId, args.toList())
    }
}
