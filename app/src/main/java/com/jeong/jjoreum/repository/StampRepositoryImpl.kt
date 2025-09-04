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
import com.jeong.jjoreum.R
import com.jeong.jjoreum.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StampRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
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
                return Result.failure(
                    Exception(
                        context.getString(R.string.permission_required_message)
                    )
                )
            }

            val location = fusedLocationClient.lastLocation.await()
                ?: return Result.failure(Exception(context.getString(R.string.location_unavailable)))

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
                        context.getString(R.string.distance_warning, distance)
                    )
                )
            }

            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception(context.getString(R.string.login_required))
            )

            firestore.runBatch { batch ->
                batch.update(
                    firestore.collection(
                        Constants.COLLECTION_USER_INFO
                    ).document(uid),
                    "${Constants.FIELD_STAMPED_OREUMS}.${oreumIdx}", oreumName
                )
                batch.update(
                    firestore.collection(
                        Constants.COLLECTION_OREUM_INFO
                    ).document(oreumIdx),
                    Constants.FIELD_STAMP, FieldValue.increment(1)
                )
            }.await()

            return Result.success(Unit)

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
