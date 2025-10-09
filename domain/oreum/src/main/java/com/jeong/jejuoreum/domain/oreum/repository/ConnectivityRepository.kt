package com.jeong.jejuoreum.domain.oreum.repository

import com.jeong.jejuoreum.core.common.network.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    fun observeStatus(): Flow<NetworkStatus>
    suspend fun getCurrentStatus(): NetworkStatus
}
