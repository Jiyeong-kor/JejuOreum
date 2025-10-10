package com.jeong.jejuoreum.domain.review.usecase

import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(oreumIdx: String, userId: String): Result<Unit> =
        reviewRepository.deleteReview(oreumIdx, userId)
}
