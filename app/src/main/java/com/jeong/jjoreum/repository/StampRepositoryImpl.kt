package com.jeong.jjoreum.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StampRepositoryImpl @Inject constructor(
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : StampRepository {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun tryStamp(
        oreumIdx: String,
        oreumName: String,
        oreumLat: Double,
        oreumLng: Double
    ): Result<Unit> {
        try {
            val permission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return Result.failure(Exception("위치 권한이 필요합니다."))
            }

            val location = fusedLocationClient.lastLocation.await()
                ?: return Result.failure(Exception("현재 위치를 가져올 수 없습니다."))

            val userLocation = Location("").apply {
                latitude = location.latitude
                longitude = location.longitude
            }

            val oreumLocation = Location("").apply {
                latitude = oreumLat
                longitude = oreumLng
            }

            val distance = userLocation.distanceTo(oreumLocation)
            if (distance > 200) {
                return Result.failure(
                    Exception(
                        "오름과의 거리가 ${"%.2f".format(distance)}m 입니다. " +
                                "200m 이내로 가까이 가주세요."
                    )
                )
            }

            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("로그인이 필요합니다.")
            )

            firestore.runBatch { batch ->
                batch.update(
                    firestore.collection(
                        "user_info_col"
                    ).document(uid),
                    "stampedOreums.$oreumIdx", oreumName
                )
                batch.update(
                    firestore.collection(
                        "oreum_info_col"
                    ).document(oreumIdx),
                    "stamp", FieldValue.increment(1)
                )
            }.await()

            return Result.success(Unit)

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}