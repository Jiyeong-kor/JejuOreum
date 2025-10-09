package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.core.common.network.NetworkStatus
import com.jeong.jejuoreum.domain.oreum.repository.ConnectivityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveConnectivityStatusUseCase @Inject constructor(
    private val repository: ConnectivityRepository
) {
    operator fun invoke(): Flow<NetworkStatus> = repository.observeStatus()
}
