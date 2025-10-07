package com.jeong.feature.main.domain.usecase

import com.jeong.core.utils.network.NetworkStatus
import com.jeong.feature.main.domain.repository.ConnectivityRepository
import javax.inject.Inject

class GetCurrentConnectivityStatusUseCase @Inject constructor(
    private val repository: ConnectivityRepository
) {
    suspend operator fun invoke(): NetworkStatus = repository.getCurrentStatus()
}
