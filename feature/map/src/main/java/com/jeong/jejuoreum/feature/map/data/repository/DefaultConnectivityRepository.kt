package com.jeong.jejuoreum.feature.main.data.repository

import com.jeong.jejuoreum.core.utils.network.NetworkMonitor
import com.jeong.jejuoreum.core.utils.network.NetworkStatus
import com.jeong.jejuoreum.feature.main.domain.repository.ConnectivityRepository
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
