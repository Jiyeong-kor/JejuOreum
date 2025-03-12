package com.jeong.jjoreum.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * 네트워크 상태를 확인하는 클래스
 * @param context 애플리케이션 컨텍스트
 */
class NetworkManager(private val context: Context) {

    /**
     * 현재 기기가 네트워크에 연결되어 있는지 확인
     * @return 네트워크 연결 여부 (true: 연결됨, false: 연결되지 않음)
     */
    fun checkNetworkState(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true // Wi-Fi 연결 확인
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true // 모바일 데이터 연결 확인
            else -> false
        }
    }
}