package com.jeong.core.utils.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    fun observe(): Flow<NetworkStatus>
    suspend fun getCurrentStatus(): NetworkStatus
}

sealed interface NetworkStatus {
    data object Available : NetworkStatus
    data object Unavailable : NetworkStatus
    data object Losing : NetworkStatus
    data object Lost : NetworkStatus
    data object Unknown : NetworkStatus
}
