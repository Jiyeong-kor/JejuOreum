package com.jeong.jejuoreum.app.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.jeong.jejuoreum.data.remote.NetworkMonitor
import com.jeong.jejuoreum.data.remote.NetworkStatus
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Singleton
class DefaultNetworkMonitor @Inject constructor(
    private val connectivityManager: ConnectivityManager,
) : NetworkMonitor {

    override fun observe(): Flow<NetworkStatus> =
        callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    sendStatus(NetworkStatus.Available)
                }

                override fun onLost(network: Network) {
                    sendStatus(NetworkStatus.Lost)
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    sendStatus(NetworkStatus.Losing)
                }

                override fun onUnavailable() {
                    sendStatus(NetworkStatus.Unavailable)
                }
            }

            registerCallback(callback)
            sendStatus(resolveCurrentStatus())

            awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
        }.distinctUntilChanged()

    override suspend fun getCurrentStatus(): NetworkStatus = resolveCurrentStatus()

    private fun ProducerScope<NetworkStatus>.sendStatus(status: NetworkStatus) {
        trySend(status)
    }

    private fun resolveCurrentStatus(): NetworkStatus {
        val activeNetwork = connectivityManager.activeNetwork ?: return NetworkStatus.Unavailable
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: return NetworkStatus.Unavailable

        val hasValidatedInternet =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))

        return if (hasValidatedInternet) {
            NetworkStatus.Available
        } else {
            NetworkStatus.Unavailable
        }
    }

    private fun registerCallback(callback: ConnectivityManager.NetworkCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(callback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, callback)
        }
    }

    companion object {
        fun create(context: Context): DefaultNetworkMonitor {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return DefaultNetworkMonitor(connectivityManager)
        }
    }
}
