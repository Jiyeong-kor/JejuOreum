package com.jeong.feature.main.domain.repository

import com.jeong.core.utils.network.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    fun observeStatus(): Flow<NetworkStatus>
    suspend fun getCurrentStatus(): NetworkStatus
}
