package com.jeong.jejuoreum.domain.review.repository

import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun observeReviews(oreumIdx: String): Flow<Resource<List<ReviewItem>>>
    suspend fun fetchReviews(oreumIdx: String): Result<List<ReviewItem>>
    suspend fun submitReview(oreumIdx: String, review: ReviewItem): Result<Unit>
    suspend fun toggleReviewLike(oreumIdx: String, userId: String): Result<Unit>
    suspend fun deleteReview(oreumIdx: String, userId: String): Result<Unit>
}
