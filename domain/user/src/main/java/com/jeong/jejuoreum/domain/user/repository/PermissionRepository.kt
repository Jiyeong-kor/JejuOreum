package com.jeong.jejuoreum.domain.user.repository

interface PermissionRepository {
    suspend fun isLocationPermissionGranted(): Result<Boolean>
    suspend fun setLocationPermissionGranted(granted: Boolean): Result<Unit>
}
