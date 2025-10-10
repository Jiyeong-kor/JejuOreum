package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import javax.inject.Inject

class GetOreumReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {

    suspend operator fun invoke(oreumIdx: String): Result<List<ReviewItem>> =
        reviewRepository.fetchReviews(oreumIdx)
}
