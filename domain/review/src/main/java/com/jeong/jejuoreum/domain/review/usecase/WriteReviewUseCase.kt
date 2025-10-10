package com.jeong.jejuoreum.domain.review.usecase

import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import javax.inject.Inject

class WriteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(oreumIdx: String, review: ReviewItem): Result<Unit> =
        reviewRepository.writeReview(oreumIdx, review)
}
