package com.jeong.jejuoreum.feature.map.domain.repository

import com.jeong.jejuoreum.data.remote.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    fun observeStatus(): Flow<NetworkStatus>
    suspend fun getCurrentStatus(): NetworkStatus
}
