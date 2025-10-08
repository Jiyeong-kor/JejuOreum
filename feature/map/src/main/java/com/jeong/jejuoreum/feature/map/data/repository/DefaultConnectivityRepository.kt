package com.jeong.feature.main.data.repository

import com.jeong.core.utils.network.NetworkMonitor
import com.jeong.core.utils.network.NetworkStatus
import com.jeong.feature.main.domain.repository.ConnectivityRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DefaultConnectivityRepository @Inject constructor(
    private val networkMonitor: NetworkMonitor,
) : ConnectivityRepository {

    override fun observeStatus(): Flow<NetworkStatus> = networkMonitor.observe()

    override suspend fun getCurrentStatus(): NetworkStatus = networkMonitor.getCurrentStatus()
}
