package com.jeong.jejuoreum.domain.review.usecase

import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import javax.inject.Inject

class ToggleReviewLikeUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(oreumIdx: String, userId: String): Result<Unit> =
        reviewRepository.toggleReviewLike(oreumIdx, userId)
}
