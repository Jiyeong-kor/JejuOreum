package com.jeong.jejuoreum.feature.main.domain.usecase

import com.jeong.jejuoreum.core.utils.network.NetworkStatus
import com.jeong.jejuoreum.feature.main.domain.repository.ConnectivityRepository
import javax.inject.Inject

class GetCurrentConnectivityStatusUseCase @Inject constructor(
    private val repository: ConnectivityRepository
) {
    suspend operator fun invoke(): NetworkStatus = repository.getCurrentStatus()
}
