package com.jeong.jjoreum.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.data.local.PreferenceManager

/**
 * 사용자의 현재 위치와 오름(oreum)의 위치를 비교하여
 * 일정 범위 내에 있으면 스탬프를 Firestore에 저장하는 클래스
 * @param context 애플리케이션 컨텍스트
 */
class SaveStamp(private val context: Context) {

    // 위치 정보를 제공하는 클라이언트
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * 위치 권한을 확인하고, 권한이 있으면 오름 위치와 비교 후 스탬프 저장
     * @param oreumLatitude 오름의 위도
     * @param oreumLongitude 오름의 경도
     * @param onSuccess 스탬프 저장 성공 콜백
     * @param onFailure 스탬프 저장 실패 콜백 (문자열 메시지 포함)
     */
    fun checkAndSaveStamp(oreumLatitude: Double, oreumLongitude: Double, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        // 위치 권한 체크
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onFailure("위치 권한이 필요합니다.")
            return
        }

        // 마지막 위치 정보 가져오기
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location == null) {
                onFailure("현재 위치를 가져올 수 없습니다.")
                return@addOnSuccessListener
            }

            // 사용자 위치와 오름 위치 생성
            val userLocation = Location("").apply {
                latitude = location.latitude
                longitude = location.longitude
            }

            val oreumLocation = Location("").apply {
                latitude = oreumLatitude
                longitude = oreumLongitude
            }

            // 두 좌표 간 거리 계산
            val distance = userLocation.distanceTo(oreumLocation)

            // 특정 범위(기본 5m) 이내라면 스탬프 저장 진행
            if (distance <= 5) {
                saveStampToFirestore(onSuccess, onFailure)
            } else {
                onFailure("오름과의 거리가 5m 이내여야 스탬프를 찍을 수 있습니다. (현재 거리: ${distance}m)")
            }
        }.addOnFailureListener {
            onFailure("현재 위치를 가져오는 데 실패했습니다.")
        }
    }

    /**
     * Firestore에 스탬프 정보를 저장하는 함수
     * @param onSuccess 성공 시 콜백
     * @param onFailure 실패 시 콜백
     */
    private fun saveStampToFirestore(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        // 사용자 닉네임 확인
        val userNickname = PreferenceManager.getInstance(context).getNickname()
        if (userNickname.isBlank()) {
            onFailure("사용자 정보가 없습니다.")
            return
        }

        val stampData = hashMapOf(
            "nickname" to userNickname,
            "timestamp" to System.currentTimeMillis()
        )

        // Firestore에 저장
        FirebaseFirestore.getInstance()
            .collection("user_info_col")
            .document(userNickname)
            .collection("stampedOreums")
            .add(stampData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure("스탬프 저장 실패")
            }
    }
}
