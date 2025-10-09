package com.jeong.jejuoreum.feature.map.data.repository

import com.jeong.jejuoreum.data.remote.NetworkMonitor
import com.jeong.jejuoreum.data.remote.NetworkStatus
import com.jeong.jejuoreum.feature.map.domain.repository.ConnectivityRepository
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
