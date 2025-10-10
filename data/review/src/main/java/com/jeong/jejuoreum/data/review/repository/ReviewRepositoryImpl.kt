package com.jeong.jejuoreum.data.review.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.core.common.error.toDomainError
import com.jeong.jejuoreum.core.common.firestore.FirestoreConstants
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.common.result.mapToDomainError
import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

@Singleton
internal class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ReviewRepository {

    override fun observeReviews(oreumIdx: String): Flow<Resource<List<ReviewItem>>> {
        return flow {
            emit(Resource.Loading)
            emit(Resource.Success(loadReviews(oreumIdx)))
        }.catch { throwable ->
            val domainError = throwable.toDomainError()
            emit(Resource.Error(domainError))
        }
    }

    override suspend fun fetchReviews(oreumIdx: String): Result<List<ReviewItem>> =
        runCatching { loadReviews(oreumIdx) }
            .mapToDomainError()

    override suspend fun submitReview(oreumIdx: String, review: ReviewItem): Result<Unit> {
        val userId = auth.currentUser?.uid
            ?: return Result.failure(DomainError.AuthenticationRequired)

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

        return runCatching {
            docRef.set(reviewMap).await()
        }.mapToDomainError()
            .map { Unit }
    }

    override suspend fun toggleReviewLike(oreumIdx: String, userId: String): Result<Unit> =
        runCatching {
            val docRef = firestore.collection(FirestoreConstants.COLLECTION_REVIEWS)
                .document(oreumIdx)
                .collection(FirestoreConstants.SUBCOLLECTION_ITEMS)
                .document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentLiked = snapshot.getBoolean(FirestoreConstants.FIELD_IS_LIKED) ?: false
                val currentLikeNum = snapshot.getLong(FirestoreConstants.FIELD_REVIEW_LIKE_NUM) ?: 0L
                val newLikeNum = if (currentLiked) currentLikeNum - 1 else currentLikeNum + 1
                transaction.update(
                    docRef,
                    mapOf(
                        FirestoreConstants.FIELD_IS_LIKED to !currentLiked,
                        FirestoreConstants.FIELD_REVIEW_LIKE_NUM to newLikeNum
                    )
                )
            }.await()
        }.mapToDomainError()
            .map { Unit }

    override suspend fun deleteReview(oreumIdx: String, userId: String): Result<Unit> =
        runCatching {
            firestore.collection(FirestoreConstants.COLLECTION_REVIEWS)
                .document(oreumIdx)
                .collection(FirestoreConstants.SUBCOLLECTION_ITEMS)
                .document(userId)
                .delete()
                .await()
        }.mapToDomainError()
            .map { Unit }

    private suspend fun loadReviews(oreumIdx: String): List<ReviewItem> {
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
}
