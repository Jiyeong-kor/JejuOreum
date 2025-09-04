package com.jeong.jjoreum.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.data.model.entity.ReviewItem
import com.jeong.jjoreum.util.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ReviewRepository {

    override suspend fun getReviews(oreumIdx: String): List<ReviewItem> {
        val snapshot = firestore.collection(Constants.COLLECTION_REVIEWS)
            .document(oreumIdx)
            .collection(Constants.SUBCOLLECTION_ITEMS)
            .get().await()

        return snapshot.mapNotNull { doc ->
            val userId = doc.getString(Constants.FIELD_USER_ID) ?: return@mapNotNull null
            val userNickname = doc.getString(Constants.FIELD_USER_NICKNAME) ?: ""
            val userReview = doc.getString(Constants.FIELD_USER_REVIEW) ?: ""
            val userTime = doc.getTimestamp(Constants.FIELD_USER_TIME)?.toDate()?.time ?: 0L
            val reviewLikeNum = doc.getLong(Constants.FIELD_REVIEW_LIKE_NUM)?.toInt() ?: 0
            val isLiked = doc.getBoolean(Constants.FIELD_IS_LIKED) ?: false

            ReviewItem(userId, userNickname, userReview, userTime, reviewLikeNum, isLiked)
        }
    }

    override suspend fun writeReview(oreumIdx: String, review: ReviewItem): Result<Unit> {
        val userId = auth.currentUser?.uid
            ?: return Result.failure(Exception(Constants.MESSAGE_LOGIN_REQUIRED))
        val docRef = firestore.collection(Constants.COLLECTION_REVIEWS)
            .document(oreumIdx)
            .collection(Constants.SUBCOLLECTION_ITEMS)
            .document(userId)

        val reviewMap = mapOf(
            Constants.FIELD_USER_ID to review.userId,
            Constants.FIELD_USER_NICKNAME to review.userNickname,
            Constants.FIELD_USER_REVIEW to review.userReview,
            Constants.FIELD_USER_TIME to java.sql.Timestamp(review.userTime),
            Constants.FIELD_REVIEW_LIKE_NUM to review.reviewLikeNum,
            Constants.FIELD_IS_LIKED to review.isLiked
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
            val docRef = firestore.collection(Constants.COLLECTION_REVIEWS)
                .document(oreumIdx)
                .collection(Constants.SUBCOLLECTION_ITEMS)
                .document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentLiked = snapshot.getBoolean(Constants.FIELD_IS_LIKED) ?: false
                val currentLikeNum = snapshot.getLong(Constants.FIELD_REVIEW_LIKE_NUM) ?: 0L

                transaction.update(
                    docRef, mapOf(
                        Constants.FIELD_IS_LIKED to !currentLiked,
                        Constants.FIELD_REVIEW_LIKE_NUM to (
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
            firestore.collection(Constants.COLLECTION_REVIEWS)
                .document(oreumIdx)
                .collection(Constants.SUBCOLLECTION_ITEMS)
                .document(userId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
