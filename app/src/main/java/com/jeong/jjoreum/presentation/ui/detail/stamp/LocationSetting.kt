//package com.jeong.jjoreum.ui.detail.stamp
//
//import android.Manifest
//import android.content.Intent
//import android.content.IntentSender
//import android.content.pm.PackageManager
//import android.location.Location
//import android.os.Bundle
//import android.view.View
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import com.google.android.gms.common.api.ResolvableApiException
//import com.google.android.gms.location.*
//import com.google.android.gms.tasks.CancellationToken
//import com.google.android.gms.tasks.CancellationTokenSource
//import com.google.android.gms.tasks.OnTokenCanceledListener
//import com.google.android.gms.tasks.Task
//import com.jeong.jjoreum.presentation.ui.dialog.NetworkDialog
//import com.jeong.jjoreum.common.OreumNetworkManager
//import com.jeong.jjoreum.common.logMessage
//import com.jeong.jjoreum.common.toastMessage
//import com.jeong.jjoreum.databinding.ActivitySettingLocationBinding
//import com.jeong.jjoreum.ui.detail.DetailActivity
//import com.jeong.jjoreum.R
//
///**
// * 사용자의 현재 위치를 확인하고 스탬프 인증을 시도하는 로직 예시
// * @constructor AppCompatActivity를 상속받아, 네트워크 연결 여부와 위치 정보를 체크
// */
//
//class LocationSetting : AppCompatActivity() {
//
//    private lateinit var binding: ActivitySettingLocationBinding
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySettingLocationBinding.inflate(layoutInflater).also {
//            setContentView(it.root)
//        }
//
//        networkCheck()
//    }
//
//    private fun networkCheck() {
//        val status = OreumNetworkManager(applicationContext).checkNetworkState()
//        if (status) {
//            checkLocationCurrentDevice()
//        } else {
//            val dialog = NetworkDialog()
//            dialog.show(supportFragmentManager, "NetworkDialogFragment")
//            dialog.myDialogInterface = object : NetworkDialog.DialogListener {
//                override fun onDialogPositiveClick() {
//                    val againCheckStatus =
//                        OreumNetworkManager(applicationContext).checkNetworkState()
//                    if (againCheckStatus) {
//                        dialog.dismiss()
//                        checkLocationCurrentDevice()
//                    } else {
//                        toastMessage("네트워크 상태를 확인해주세요.")
//                    }
//                }
//            }
//        }
//    }
//
//    private fun checkLocationCurrentDevice() {
//        val locationIntervalTime = 3000L
//        val locationRequest =
//            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationIntervalTime)
//                .setWaitForAccurateLocation(true)
//                .setMinUpdateIntervalMillis(locationIntervalTime)
//                .setMaxUpdateDelayMillis(locationIntervalTime)
//                .build()
//
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
//
//        val settingsClient: SettingsClient = LocationServices.getSettingsClient(this)
//        val task: Task<LocationSettingsResponse> = settingsClient.checkLocationSettings(builder)
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        task.addOnSuccessListener {
//            currentLocation()
//        }
//
//        task.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                try {
//                    val intentSenderRequest =
//                        IntentSenderRequest.Builder(exception.resolution).build()
//                    resolutionForResult.launch(intentSenderRequest)
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    logMessage("${sendEx.message}")
//                }
//            }
//        }
//    }
//
//    private val resolutionForResult =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                checkLocationCurrentDevice()
//            } else {
//                toastMessage("위치정보 설정은 반드시 On 상태여야 합니다.")
//                checkLocationCurrentDevice()
//            }
//        }
//
//    private fun currentLocation() {
//        binding.settingLayout.visibility = View.VISIBLE
//        val oreumName = intent.getStringExtra("oreumName")
//        binding.settingText.text = resources.getString(R.string.setting_location_text, oreumName)
//        if (
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        fusedLocationClient.getCurrentLocation(
//            Priority.PRIORITY_HIGH_ACCURACY,
//            object : CancellationToken() {
//                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
//                    CancellationTokenSource().token
//
//                override fun isCancellationRequested() = false
//            })
//            .addOnSuccessListener { location: Location? ->
//                if (location == null)
//                    logMessage("Cannot get location.")
//                else {
//                    val lat = location.latitude
//                    val lon = location.longitude
//                    compareLocation(lat, lon)
//                }
//            }
//    }
//
//    private fun compareLocation(lat: Double, lon: Double) {
//        val oreumLatitude = intent.getDoubleExtra("oreumLatitude", 0.0)
//        val oreumLongitude = intent.getDoubleExtra("oreumLongitude", 0.0)
//        val oreumLocation = Location("oreumLocation")
//        oreumLocation.latitude = oreumLatitude
//        oreumLocation.longitude = oreumLongitude
//
//        val currentLocation = Location("currentLocation")
//        currentLocation.latitude = lat
//        currentLocation.longitude = lon
//
//        val distance = oreumLocation.distanceTo(currentLocation)
//        stampBoolean(distance)
//    }
//
//    private fun stampBoolean(distance: Float) {
//        val stampBoolean = distance <= 50.0
//        val intent = Intent(this, DetailActivity::class.java).apply {
//            putExtra("stampBoolean", stampBoolean)
//        }
//        setResult(RESULT_OK, intent)
//        finish()
//    }
//}
