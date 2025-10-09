package com.jeong.jejuoreum.feature.map.domain.usecase

import com.jeong.jejuoreum.data.remote.NetworkStatus
import com.jeong.jejuoreum.feature.map.domain.repository.ConnectivityRepository
import javax.inject.Inject

class GetCurrentConnectivityStatusUseCase @Inject constructor(
    private val repository: ConnectivityRepository
) {
    suspend operator fun invoke(): NetworkStatus = repository.getCurrentStatus()
}
