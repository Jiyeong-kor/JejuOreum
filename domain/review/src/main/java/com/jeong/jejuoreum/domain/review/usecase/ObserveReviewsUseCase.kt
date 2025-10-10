package com.jeong.jejuoreum.domain.review.usecase

import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    operator fun invoke(oreumIdx: String): Flow<Resource<List<ReviewItem>>> {
        return reviewRepository.observeReviews(oreumIdx)
    }
}
