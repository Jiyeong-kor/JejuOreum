package com.jeong.jejuoreum.core.testing.fake

import com.jeong.jejuoreum.domain.user.repository.PermissionRepository

class FakePermissionRepository(
    initialGranted: Boolean = false,
) : PermissionRepository {

    var isGranted: Boolean = initialGranted
    var setResult: Result<Unit> = Result.success(Unit)

    override suspend fun isLocationPermissionGranted(): Result<Boolean> = Result.success(isGranted)

    override suspend fun setLocationPermissionGranted(granted: Boolean): Result<Unit> {
        isGranted = granted
        return setResult
    }
}
