package com.jeong.jejuoreum.feature.main.domain.repository

import com.jeong.jejuoreum.core.utils.network.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    fun observeStatus(): Flow<NetworkStatus>
    suspend fun getCurrentStatus(): NetworkStatus
}
