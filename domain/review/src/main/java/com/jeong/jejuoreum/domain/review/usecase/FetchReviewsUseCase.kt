package com.jeong.jejuoreum.domain.review.usecase

import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import javax.inject.Inject

class FetchReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(oreumIdx: String): Result<List<ReviewItem>> =
        reviewRepository.fetchReviews(oreumIdx)
}
