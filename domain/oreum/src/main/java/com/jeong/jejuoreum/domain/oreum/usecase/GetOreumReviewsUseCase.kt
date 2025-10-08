package com.jeong.domain.usecase.oreum

import com.jeong.domain.entity.ReviewItem
import com.jeong.domain.repository.ReviewRepository
import javax.inject.Inject

class GetOreumReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {

    suspend operator fun invoke(oreumIdx: String): Result<List<ReviewItem>> =
        runCatching { reviewRepository.getReviews(oreumIdx) }
}
