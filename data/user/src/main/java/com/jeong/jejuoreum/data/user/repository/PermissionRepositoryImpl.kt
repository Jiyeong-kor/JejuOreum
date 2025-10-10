package com.jeong.jejuoreum.data.user.repository

import com.jeong.jejuoreum.core.common.result.mapToDomainError
import com.jeong.jejuoreum.data.local.permission.PermissionLocalDataSource
import com.jeong.jejuoreum.domain.user.repository.PermissionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PermissionRepositoryImpl @Inject constructor(
    private val permissionLocalDataSource: PermissionLocalDataSource,
) : PermissionRepository {

    override suspend fun isLocationPermissionGranted(): Result<Boolean> =
        runCatching { permissionLocalDataSource.isLocationPermissionGranted() }
            .mapToDomainError()

    override suspend fun setLocationPermissionGranted(granted: Boolean): Result<Unit> =
        runCatching { permissionLocalDataSource.setLocationPermissionGranted(granted) }
            .mapToDomainError()
}
