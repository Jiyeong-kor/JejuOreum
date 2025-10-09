package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.repository.PermissionRepository

class UpdateLocationPermissionUseCase(
    private val permissionRepository: PermissionRepository,
) {
    suspend operator fun invoke(granted: Boolean): Result<Unit> =
        permissionRepository.setLocationPermissionGranted(granted)
}
