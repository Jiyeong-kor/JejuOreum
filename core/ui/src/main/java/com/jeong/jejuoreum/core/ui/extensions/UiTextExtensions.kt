package com.jeong.jejuoreum.core.ui.extensions

import android.content.Context
import com.jeong.jejuoreum.core.architecture.UiText

fun UiText.asString(context: Context): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.StringResource -> context.getString(resId, *args.toTypedArray())
}
