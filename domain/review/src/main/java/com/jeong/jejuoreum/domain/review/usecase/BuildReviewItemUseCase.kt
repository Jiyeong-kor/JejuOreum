package com.jeong.jejuoreum.domain.review.usecase

import com.jeong.jejuoreum.domain.review.entity.ReviewItem

class BuildReviewItemUseCase {
    operator fun invoke(
        id: String,
        content: String,
        author: String,
        createdAt: Long
    ): ReviewItem {
        return ReviewItem(
            id = id,
            content = content,
            author = author,
            createdAt = createdAt
        )
    }
