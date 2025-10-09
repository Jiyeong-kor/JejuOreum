package com.jeong.jejuoreum.domain.usecase

import com.jeong.jejuoreum.domain.repository.PermissionRepository

class IsLocationPermissionGrantedUseCase(
    private val permissionRepository: PermissionRepository,
) {
    suspend operator fun invoke(): Result<Boolean> =
        permissionRepository.isLocationPermissionGranted()
}
