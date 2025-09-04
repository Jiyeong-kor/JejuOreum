package com.jeong.jjoreum.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.jeong.jjoreum.R
import com.jeong.jjoreum.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInteractionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @param:ApplicationContext private val context: Context
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
        val uid = getUserId() ?: throw IllegalStateException(
            context.getString(R.string.login_required)
        )
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
                Timber.d("✅ [쓰기 성공] Oreum %s -> %s", oreumIdx, newIsFavorite)
                count
            }.await()
        } catch (e: Exception) {
            Timber.e(e, "❌ [쓰기 실패] 원인")
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
        val defaultNickname = context.getString(R.string.default_nickname)
        val uid = getUserId() ?: return defaultNickname
        val doc = firestore.collection(
            Constants.COLLECTION_USER_INFO
        ).document(uid).get().await()
        return doc.getString(Constants.FIELD_NICKNAME) ?: defaultNickname
    }
}
