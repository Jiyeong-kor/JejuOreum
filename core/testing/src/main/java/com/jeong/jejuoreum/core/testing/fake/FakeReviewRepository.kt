package com.jeong.jejuoreum.core.testing.fake

import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.common.result.ResourceError
import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeReviewRepository : ReviewRepository {

    private val reviewsState = MutableStateFlow<Resource<List<ReviewItem>>>(Resource.Loading)

    var fetchResult: Result<List<ReviewItem>> = Result.success(emptyList())
    var submitResult: Result<Unit> = Result.success(Unit)
    var toggleResult: Result<Unit> = Result.success(Unit)
    var deleteResult: Result<Unit> = Result.success(Unit)

    var lastSubmittedReview: ReviewItem? = null
    var lastToggledReviewId: String? = null
    var lastDeletedReviewId: String? = null

    override fun observeReviews(oreumIdx: String): Flow<Resource<List<ReviewItem>>> = reviewsState

    override suspend fun fetchReviews(oreumIdx: String): Result<List<ReviewItem>> {
        fetchResult.onSuccess { reviews -> reviewsState.value = Resource.Success(reviews) }
        fetchResult.onFailure { throwable ->
            reviewsState.value = Resource.Error(ResourceError.Unknown(throwable))
        }
        return fetchResult
    }

    override suspend fun submitReview(oreumIdx: String, review: ReviewItem): Result<Unit> {
        lastSubmittedReview = review
        return submitResult
    }

    override suspend fun toggleReviewLike(oreumIdx: String, userId: String): Result<Unit> {
        lastToggledReviewId = userId
        return toggleResult
    }

    override suspend fun deleteReview(oreumIdx: String, userId: String): Result<Unit> {
        lastDeletedReviewId = userId
        return deleteResult
    }

    fun emitReviews(resource: Resource<List<ReviewItem>>) {
        reviewsState.value = resource
    }
}
