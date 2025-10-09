package com.jeong.jejuoreum.domain.repository

import com.jeong.jejuoreum.domain.entity.ReviewItem

interface ReviewRepository {
    suspend fun getReviews(oreumIdx: String): List<ReviewItem>
    suspend fun writeReview(oreumIdx: String, review: ReviewItem): Result<Unit>
    suspend fun toggleReviewLike(oreumIdx: String, userId: String): Result<Unit>
    suspend fun deleteReview(oreumIdx: String, userId: String): Result<Unit>
}
