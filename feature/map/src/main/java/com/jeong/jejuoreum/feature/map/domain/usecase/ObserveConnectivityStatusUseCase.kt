package com.jeong.jejuoreum.feature.map.domain.usecase

import com.jeong.jejuoreum.data.remote.NetworkStatus
import com.jeong.jejuoreum.feature.map.domain.repository.ConnectivityRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveConnectivityStatusUseCase @Inject constructor(
    private val repository: ConnectivityRepository
) {
    operator fun invoke(): Flow<NetworkStatus> = repository.observeStatus()
}
