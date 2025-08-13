package com.jeong.jjoreum.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
            "user_info_col"
        ).document(uid).get().await()
        val map = doc.get("favorites") as? Map<*, *> ?: return false
        return map[oreumIdx] as? Boolean ?: false
    }

    override suspend fun getStampStatus(oreumIdx: String): Boolean {
        val uid = getUserId() ?: return false
        val doc = firestore.collection(
            "user_info_col"
        ).document(uid).get().await()
        val map = doc.get("stampedOreums") as? Map<*, *> ?: return false
        return map.containsKey(oreumIdx)
    }

    override suspend fun toggleFavorite(oreumIdx: String, newIsFavorite: Boolean): Int {
        val uid = getUserId() ?: throw IllegalStateException("로그인 필요")
        val userDoc = firestore.collection(
            "user_info_col"
        ).document(uid)
        val oreumDoc = firestore.collection(
            "oreum_info_col"
        ).document(oreumIdx)

        return firestore.runTransaction { tx ->
            val userSnap = tx.get(userDoc)
            val oreumSnap = tx.get(oreumDoc)

            val favorites = (userSnap.get("favorites") as? Map<String, Boolean>)?.toMutableMap()
                ?: mutableMapOf()
            var count = oreumSnap.getLong("favorite")?.toInt() ?: 0

            if (newIsFavorite) {
                favorites[oreumIdx] = true
                count++
            } else {
                favorites.remove(oreumIdx)
                if (count > 0) count--
            }

            tx.set(
                userDoc,
                mapOf("favorites" to favorites), SetOptions.merge()
            )
            tx.set(
                oreumDoc,
                mapOf("favorite" to count), SetOptions.merge()
            )
            count
        }.await()
    }

    override suspend fun getAllFavoriteStatus(): Map<String, Boolean> {
        val uid = getUserId() ?: return emptyMap()
        val doc = firestore.collection(
            "user_info_col"
        ).document(uid).get().await()
        return doc.get("favorites") as? Map<String, Boolean> ?: emptyMap()
    }

    override suspend fun getAllStampStatus(): Map<String, Boolean> {
        val uid = getUserId() ?: return emptyMap()
        val doc = firestore.collection(
            "user_info_col"
        ).document(uid).get().await()
        return (doc.get("stampedOreums") as? Map<String, String>)?.mapValues { true } ?: emptyMap()
    }

    override suspend fun getCurrentUserNickname(): String {
        val uid = getUserId() ?: return "닉네임없음"
        val doc = firestore.collection(
            "user_info_col"
        ).document(uid).get().await()
        return doc.getString("nickname") ?: "닉네임없음"
    }
}
