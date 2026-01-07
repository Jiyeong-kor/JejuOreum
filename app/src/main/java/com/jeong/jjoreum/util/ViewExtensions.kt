package com.jeong.jjoreum.util

import android.os.SystemClock
import android.view.View
import com.jeong.jjoreum.R

fun View.setThrottledOnClickListener(
    intervalMs: Long = 500L,
    onClick: (View) -> Unit
) {
    setOnClickListener { view ->
        val lastClickTime = getTag(R.id.tag_throttled_click_timestamp) as? Long ?: 0L
        val now = SystemClock.elapsedRealtime()
        if (now - lastClickTime >= intervalMs) {
            setTag(R.id.tag_throttled_click_timestamp, now)
            onClick(view)
        }
    }
}
