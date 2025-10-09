package com.jeong.jejuoreum.domain.user.usecase

import com.jeong.jejuoreum.domain.user.repository.PermissionRepository

class IsLocationPermissionGrantedUseCase(
    private val permissionRepository: PermissionRepository,
) {
    suspend operator fun invoke(): Result<Boolean> =
        permissionRepository.isLocationPermissionGranted()
}
