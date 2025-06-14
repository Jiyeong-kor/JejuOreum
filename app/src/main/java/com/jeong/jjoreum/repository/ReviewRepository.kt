package com.jeong.jjoreum.repository

import com.jeong.jjoreum.data.model.entity.ReviewItem

interface ReviewRepository {
    suspend fun getReviews(oreumIdx: String): List<ReviewItem>
    suspend fun writeReview(oreumIdx: String, review: ReviewItem): Result<Unit>
    suspend fun toggleReviewLike(oreumIdx: String, userId: String): Result<Unit> // 👍 추가
    suspend fun deleteReview(oreumIdx: String, userId: String): Result<Unit>
}
