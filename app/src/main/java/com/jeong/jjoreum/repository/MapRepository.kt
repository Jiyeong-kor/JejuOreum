package com.jeong.jjoreum.repository

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.jeong.jjoreum.data.model.api.OreumRetrofitInterface
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.data.model.api.RetrofitOkHttpManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MapRepository(application: Application) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val apiService: OreumRetrofitInterface =
        RetrofitOkHttpManager.oreumRetrofitBuilder.create(OreumRetrofitInterface::class.java)

    /**
     * 오름 리스트를 API에서 가져옴
     */
    suspend fun getOreumList(): List<ResultSummary> = try {
        apiService.getOreumList().body()?.resultSummary ?: emptyList()

    } catch (e: Exception) {
        emptyList()
    }

    /**
     * 현재 위치 정보를 Flow 형태로 반환
     */
    @SuppressLint("MissingPermission")
    fun getLocationFlow(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 3000L
        ).apply {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(3000L)
            setIntervalMillis(3000L)
            setMaxUpdateDelayMillis(3000L)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }
}