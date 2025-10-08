package com.jeong.jejuoreum.data.local.permission

interface PermissionLocalDataSource {
    suspend fun setLocationPermissionGranted(granted: Boolean)
    suspend fun isLocationPermissionGranted(): Boolean
}