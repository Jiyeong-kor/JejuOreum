package com.jeong.jjoreum.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class PermissionEvent {
    data object RequestLocationPermission : PermissionEvent()
    data object PermissionGranted : PermissionEvent()
    data object PermissionDenied : PermissionEvent()
    data object PermissionDeniedPermanently : PermissionEvent()
}

@HiltViewModel
class LocationPermissionViewModel
@Inject constructor(application: Application) : AndroidViewModel(application) {

    private val _permissionEvent = MutableLiveData<PermissionEvent>()
    val permissionEvent: LiveData<PermissionEvent> get() = _permissionEvent

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("PermissionPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    private val _isLocationPermissionGranted = MutableLiveData<Boolean>()
    val isLocationPermissionGranted: LiveData<Boolean> get() = _isLocationPermissionGranted

    private val requiredPermissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    init {
        checkLocationPermission()
    }

    fun checkLocationPermission() {
        _isLocationPermissionGranted.value = areAllPermissionsGranted()
    }

    private fun areAllPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    getApplication<Application>().applicationContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun requestLocationPermission() {
        if (!areAllPermissionsGranted()) {
            _permissionEvent.value = PermissionEvent.RequestLocationPermission
        } else {
            _permissionEvent.value = PermissionEvent.PermissionGranted
        }
    }

    fun handlePermissionResult(grantResults: Map<String, Boolean>) {
        val allGranted = grantResults.values.all { it }
        if (allGranted) {
            _isLocationPermissionGranted.value = true
            editor.putBoolean("LOCATION_PERMISSION", true).apply()
            _permissionEvent.value = PermissionEvent.PermissionGranted
        } else {
            _isLocationPermissionGranted.value = false
            editor.putBoolean("LOCATION_PERMISSION", false).apply()

            var deniedPermanently = false
            for (permission in requiredPermissions) {
                if (grantResults[permission] == false &&
                    !shouldShowRequestPermissionRationale()
                ) {
                    deniedPermanently = true
                    break
                }
            }

            if (deniedPermanently) {
                _permissionEvent.value = PermissionEvent.PermissionDeniedPermanently
            } else {
                _permissionEvent.value = PermissionEvent.PermissionDenied
            }
        }
    }

    private fun shouldShowRequestPermissionRationale(): Boolean {
        return false
    }

    fun getRequiredPermissions(): Array<String> {
        return requiredPermissions
    }
}