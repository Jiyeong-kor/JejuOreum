package com.jeong.jejuoreum.data.oreum.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jejuoreum.core.common.firestore.FirestoreConstants
import com.jeong.jejuoreum.domain.oreum.repository.StampRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class StampRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : StampRepository {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun tryStamp(
        oreumIdx: String,
        oreumName: String,
        oreumLat: Double,
        oreumLng: Double
    ): Result<Unit> {
        try {
            val permission = context.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return Result.failure(Exception("Permission required"))
            }

            val location = fusedLocationClient.lastLocation.await()
                ?: return Result.failure(Exception("Location unavailable"))

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
                    Exception("Distance warning: $distance")
                )
            }

            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Login required")
            )

            firestore.runBatch { batch ->
                batch.update(
                    firestore.collection(
                        FirestoreConstants.COLLECTION_USER_INFO
                    ).document(uid),
                    "${FirestoreConstants.FIELD_STAMPED_OREUMS}.${oreumIdx}", oreumName
                )
                batch.update(
                    firestore.collection(
                        FirestoreConstants.COLLECTION_OREUM_INFO
                    ).document(oreumIdx),
                    FirestoreConstants.FIELD_STAMP, FieldValue.increment(1)
                )
            }.await()

            return Result.success(Unit)

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
