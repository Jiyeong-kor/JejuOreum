package com.jeong.jejuoreum.feature.main.domain.usecase

import com.jeong.jejuoreum.core.utils.network.NetworkStatus
import com.jeong.jejuoreum.feature.main.domain.repository.ConnectivityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveConnectivityStatusUseCase @Inject constructor(
    private val repository: ConnectivityRepository
) {
    operator fun invoke(): Flow<NetworkStatus> = repository.observeStatus()
}
