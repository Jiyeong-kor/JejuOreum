package com.jeong.domain.usecase

import com.jeong.domain.repository.PermissionRepository

class UpdateLocationPermissionUseCase(
    private val permissionRepository: PermissionRepository,
) {
    suspend operator fun invoke(granted: Boolean): Result<Unit> =
        permissionRepository.setLocationPermissionGranted(granted)
}
