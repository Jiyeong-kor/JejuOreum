package com.jeong.domain.usecase

import com.jeong.domain.repository.PermissionRepository

class IsLocationPermissionGrantedUseCase(
    private val permissionRepository: PermissionRepository,
) {
    suspend operator fun invoke(): Result<Boolean> =
        permissionRepository.isLocationPermissionGranted()
}
