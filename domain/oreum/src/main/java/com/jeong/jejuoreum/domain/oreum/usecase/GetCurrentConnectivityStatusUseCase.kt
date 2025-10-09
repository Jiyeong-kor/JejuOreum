package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.core.common.network.NetworkStatus
import com.jeong.jejuoreum.domain.oreum.repository.ConnectivityRepository
import javax.inject.Inject

class GetCurrentConnectivityStatusUseCase @Inject constructor(
    private val repository: ConnectivityRepository
) {
    suspend operator fun invoke(): NetworkStatus = repository.getCurrentStatus()
}
