package com.jeong.jejuoreum.domain.review.usecase

import com.jeong.jejuoreum.domain.review.entity.ReviewItem
import javax.inject.Inject

class BuildReviewItemUseCase @Inject constructor() {
    operator fun invoke(
        userId: String,
        nickname: String,
        content: String,
        createdAt: Long,
        likeCount: Int = 0,
        isLiked: Boolean = false,
    ): ReviewItem {
        return ReviewItem(
            userId = userId,
            userNickname = nickname,
            userReview = content,
            userTime = createdAt,
            reviewLikeNum = likeCount,
            isLiked = isLiked,
        )
    }
}
