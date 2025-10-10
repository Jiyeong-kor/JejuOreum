package com.jeong.jejuoreum.domain.review.usecase

import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository

class GetReviewsUseCase(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(oreumIdx: Int): Result<List<ReviewItem>> {
        return runCatching { reviewRepository.getReviews(oreumIdx) }
    }
}
