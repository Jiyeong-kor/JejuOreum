package com.jeong.jjoreum.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

class NetworkManager(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun checkNetworkState(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return with(networkCapabilities) {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ||
                    hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun registerNetworkCallback(onAvailable: () -> Unit, onLost: () -> Unit) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = onAvailable()
            override fun onLost(network: Network) = onLost()
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        networkCallback = callback
    }

    fun unregisterNetworkCallback() {
        networkCallback?.let(connectivityManager::unregisterNetworkCallback)
    }
}
