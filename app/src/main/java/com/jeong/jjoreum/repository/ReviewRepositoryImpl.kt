package com.jeong.jjoreum.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.data.model.entity.ReviewItem
import kotlinx.coroutines.tasks.await

class ReviewRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ReviewRepository {

    override suspend fun getReviews(oreumIdx: String): List<ReviewItem> {
        val snapshot = firestore.collection("reviews")
            .document(oreumIdx)
            .collection("items")
            .get().await()

        return snapshot.mapNotNull { doc ->
            val userId = doc.getString("userId") ?: return@mapNotNull null
            val userNickname = doc.getString("userNickname") ?: ""
            val userReview = doc.getString("userReview") ?: ""
            val userTime = doc.getTimestamp("userTime")?.toDate()?.time ?: 0L
            val reviewLikeNum = doc.getLong("reviewLikeNum")?.toInt() ?: 0
            val isLiked = doc.getBoolean("isLiked") ?: false

            ReviewItem(userId, userNickname, userReview, userTime, reviewLikeNum, isLiked)
        }
    }

    override suspend fun writeReview(oreumIdx: String, review: ReviewItem): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("로그인 필요"))
        val docRef = firestore.collection("reviews")
            .document(oreumIdx)
            .collection("items")
            .document(userId)

        val reviewMap = mapOf(
            "userId" to review.userId,
            "userNickname" to review.userNickname,
            "userReview" to review.userReview,
            "userTime" to java.sql.Timestamp(review.userTime),
            "reviewLikeNum" to review.reviewLikeNum,
            "isLiked" to review.isLiked
        )

        return try {
            docRef.set(reviewMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleReviewLike(oreumIdx: String, userId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("reviews")
                .document(oreumIdx)
                .collection("items")
                .document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentLiked = snapshot.getBoolean("isLiked") ?: false
                val currentLikeNum = snapshot.getLong("reviewLikeNum") ?: 0L

                transaction.update(
                    docRef, mapOf(
                        "isLiked" to !currentLiked,
                        "reviewLikeNum" to (if (currentLiked) currentLikeNum - 1 else currentLikeNum + 1)
                    )
                )
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(oreumIdx: String, userId: String): Result<Unit> {
        return try {
            firestore.collection("reviews")
                .document(oreumIdx)
                .collection("items")
                .document(userId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
