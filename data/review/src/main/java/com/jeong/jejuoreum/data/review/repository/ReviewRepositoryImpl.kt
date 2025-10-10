package com.jeong.jejuoreum.data.review.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jejuoreum.core.common.firestore.FirestoreConstants
import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ReviewRepository {

    override suspend fun getReviews(oreumIdx: String): List<ReviewItem> {
        val snapshot = firestore.collection(FirestoreConstants.COLLECTION_REVIEWS)
            .document(oreumIdx)
            .collection(FirestoreConstants.SUBCOLLECTION_ITEMS)
            .get().await()

        return snapshot.mapNotNull { doc ->
            val userId = doc.getString(FirestoreConstants.FIELD_USER_ID) ?: return@mapNotNull null
            val userNickname = doc.getString(FirestoreConstants.FIELD_USER_NICKNAME) ?: ""
            val userReview = doc.getString(FirestoreConstants.FIELD_USER_REVIEW) ?: ""
            val userTime =
                doc.getTimestamp(FirestoreConstants.FIELD_USER_TIME)?.toDate()?.time ?: 0L
            val reviewLikeNum = doc.getLong(FirestoreConstants.FIELD_REVIEW_LIKE_NUM)?.toInt() ?: 0
            val isLiked = doc.getBoolean(FirestoreConstants.FIELD_IS_LIKED) ?: false

            ReviewItem(userId, userNickname, userReview, userTime, reviewLikeNum, isLiked)
        }
    }

    override suspend fun writeReview(oreumIdx: String, review: ReviewItem): Result<Unit> {
        val userId = auth.currentUser?.uid
            ?: return Result.failure(Exception("Login required"))
        val docRef = firestore.collection(FirestoreConstants.COLLECTION_REVIEWS)
            .document(oreumIdx)
            .collection(FirestoreConstants.SUBCOLLECTION_ITEMS)
            .document(userId)

        val reviewMap = mapOf(
            FirestoreConstants.FIELD_USER_ID to review.userId,
            FirestoreConstants.FIELD_USER_NICKNAME to review.userNickname,
            FirestoreConstants.FIELD_USER_REVIEW to review.userReview,
            FirestoreConstants.FIELD_USER_TIME to java.sql.Timestamp(review.userTime),
            FirestoreConstants.FIELD_REVIEW_LIKE_NUM to review.reviewLikeNum,
            FirestoreConstants.FIELD_IS_LIKED to review.isLiked
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
            val docRef = firestore.collection(FirestoreConstants.COLLECTION_REVIEWS)
                .document(oreumIdx)
                .collection(FirestoreConstants.SUBCOLLECTION_ITEMS)
                .document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentLiked = snapshot.getBoolean(FirestoreConstants.FIELD_IS_LIKED) ?: false
                val currentLikeNum =
                    snapshot.getLong(FirestoreConstants.FIELD_REVIEW_LIKE_NUM) ?: 0L
                transaction.update(
                    docRef, mapOf(
                        FirestoreConstants.FIELD_IS_LIKED to !currentLiked,
                        FirestoreConstants.FIELD_REVIEW_LIKE_NUM to (
                                if (currentLiked) currentLikeNum - 1 else currentLikeNum + 1)
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
            firestore.collection(FirestoreConstants.COLLECTION_REVIEWS)
                .document(oreumIdx)
                .collection(FirestoreConstants.SUBCOLLECTION_ITEMS)
                .document(userId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
