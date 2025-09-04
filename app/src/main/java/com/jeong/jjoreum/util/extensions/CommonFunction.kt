package com.jeong.jjoreum.util.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toastMessage(@StringRes messageRes: Int) {
    Toast.makeText(this, getString(messageRes), Toast.LENGTH_SHORT).show()
}
