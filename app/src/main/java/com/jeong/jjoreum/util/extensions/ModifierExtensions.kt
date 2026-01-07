package com.jeong.jjoreum.util.extensions

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.throttleClickable(
    periodMillis: Long,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    clickable(enabled = enabled) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastClickTime >= periodMillis) {
            lastClickTime = now
            onClick()
        }
    }
}
