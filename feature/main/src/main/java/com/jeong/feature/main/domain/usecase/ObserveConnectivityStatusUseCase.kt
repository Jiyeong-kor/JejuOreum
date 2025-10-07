package com.jeong.feature.main.domain.usecase

import com.jeong.core.utils.network.NetworkStatus
import com.jeong.feature.main.domain.repository.ConnectivityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveConnectivityStatusUseCase @Inject constructor(
    private val repository: ConnectivityRepository
) {
    operator fun invoke(): Flow<NetworkStatus> = repository.observeStatus()
}
