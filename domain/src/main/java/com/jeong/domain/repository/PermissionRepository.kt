package com.jeong.domain.repository

interface PermissionRepository {
    suspend fun isLocationPermissionGranted(): Result<Boolean>
    suspend fun setLocationPermissionGranted(granted: Boolean): Result<Unit>
}
