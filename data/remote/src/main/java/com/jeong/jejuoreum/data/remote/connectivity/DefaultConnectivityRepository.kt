package com.jeong.jejuoreum.data.remote.connectivity

import com.jeong.jejuoreum.core.common.network.NetworkMonitor
import com.jeong.jejuoreum.core.common.network.NetworkStatus
import com.jeong.jejuoreum.domain.oreum.repository.ConnectivityRepository
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
