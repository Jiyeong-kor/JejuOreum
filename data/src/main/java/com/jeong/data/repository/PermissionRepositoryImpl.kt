package com.jeong.data.repository

import com.jeong.data.local.permission.PermissionLocalDataSource
import com.jeong.domain.repository.PermissionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionRepositoryImpl @Inject constructor(
    private val permissionLocalDataSource: PermissionLocalDataSource,
) : PermissionRepository {

    override suspend fun isLocationPermissionGranted(): Result<Boolean> =
        runCatching { permissionLocalDataSource.isLocationPermissionGranted() }

    override suspend fun setLocationPermissionGranted(granted: Boolean): Result<Unit> =
        runCatching { permissionLocalDataSource.setLocationPermissionGranted(granted) }
}
