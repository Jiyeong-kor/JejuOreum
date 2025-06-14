package com.jeong.jjoreum.data.local

import android.content.Context
import androidx.core.content.edit

object PermissionManager {
    private const val PREF_KEY_LOCATION_GRANTED = "location_granted"

    fun isLocationGranted(context: Context): Boolean {
        val prefs = context.getSharedPreferences("permissions", Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_KEY_LOCATION_GRANTED, false)
    }

    fun setLocationGranted(context: Context, granted: Boolean) {
        val prefs = context.getSharedPreferences("permissions", Context.MODE_PRIVATE)
        prefs.edit { putBoolean(PREF_KEY_LOCATION_GRANTED, granted) }
    }
}
