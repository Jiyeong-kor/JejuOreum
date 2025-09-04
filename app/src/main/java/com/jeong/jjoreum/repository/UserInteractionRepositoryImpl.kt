package com.jeong.jjoreum.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.jeong.jjoreum.util.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInteractionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserInteractionRepository {

    private fun getUserId(): String? = auth.currentUser?.uid

    override suspend fun getFavoriteStatus(oreumIdx: String): Boolean {
        val uid = getUserId() ?: return false
        val doc = firestore.collection(
            Constants.COLLECTION_USER_INFO
        ).document(uid).get().await()
        val map = doc.get(Constants.FIELD_FAVORITES).toStringBooleanMap()
        return map[oreumIdx] == true
    }

    override suspend fun getStampStatus(oreumIdx: String): Boolean {
        val uid = getUserId() ?: return false
        val doc = firestore.collection(
            Constants.COLLECTION_USER_INFO
        ).document(uid).get().await()
        val map = doc.get(Constants.FIELD_STAMPED_OREUMS).toStringStringMap()
        return map.containsKey(oreumIdx)
    }

    override suspend fun toggleFavorite(oreumIdx: String, newIsFavorite: Boolean): Int {
        val uid = getUserId() ?: throw IllegalStateException(Constants.MESSAGE_LOGIN_REQUIRED)
        val userDoc = firestore.collection(
            Constants.COLLECTION_USER_INFO
        ).document(uid)
        val oreumDoc = firestore.collection(
            Constants.COLLECTION_OREUM_INFO
        ).document(oreumIdx)

        return try {
            firestore.runTransaction { tx ->
                val userSnap = tx.get(userDoc)
                val oreumSnap = tx.get(oreumDoc)

                val favorites =
                    userSnap.get(Constants.FIELD_FAVORITES).toStringBooleanMap().toMutableMap()
                var count = oreumSnap.getLong(Constants.FIELD_FAVORITE)?.toInt() ?: 0

                if (newIsFavorite) {
                    favorites[oreumIdx] = true
                    count++
                } else {
                    favorites[oreumIdx] = false
                    if (count > 0) count--
                }
                tx.set(
                    userDoc,
                    mapOf(Constants.FIELD_FAVORITES to favorites), SetOptions.merge()
                )
                tx.set(
                    oreumDoc,
                    mapOf(Constants.FIELD_FAVORITE to count), SetOptions.merge()
                )
                Log.d(
                    "FavoriteDebug",
                    "✅ [쓰기 성공] Oreum $oreumIdx -> $newIsFavorite"
                )
                count
            }.await()
        } catch (e: Exception) {
            Log.e("FavoriteDebug", "❌ [쓰기 실패] 원인: ", e)
            val oreumSnap = oreumDoc.get().await()
            oreumSnap.getLong(Constants.FIELD_FAVORITE)?.toInt() ?: 0
        }
    }

    override suspend fun getAllFavoriteStatus(): Map<String, Boolean> {
        val uid = getUserId() ?: return emptyMap()
        val doc = firestore.collection(
            Constants.COLLECTION_USER_INFO
        ).document(uid).get(Source.SERVER).await()
        return doc.get(Constants.FIELD_FAVORITES).toStringBooleanMap()
    }

    override suspend fun getAllStampStatus(): Map<String, Boolean> {
        val uid = getUserId() ?: return emptyMap()
        val doc = firestore.collection(
            Constants.COLLECTION_USER_INFO
        ).document(uid).get().await()
        return doc.get(Constants.FIELD_STAMPED_OREUMS).toStringStringMap().mapValues { true }
    }

    override suspend fun getCurrentUserNickname(): String {
        val uid = getUserId() ?: return Constants.DEFAULT_NICKNAME
        val doc = firestore.collection(
            Constants.COLLECTION_USER_INFO
        ).document(uid).get().await()
        return doc.getString(Constants.FIELD_NICKNAME) ?: Constants.DEFAULT_NICKNAME
    }
}
