package com.jeong.jejuoreum.feature.detail.presentation.review

import com.jeong.jejuoreum.domain.review.entity.ReviewItem

data class ReviewUiModel(
    val userId: String,
    val userNickname: String,
    val userReview: String,
    val userTime: Long,
    val reviewLikeNum: Int,
    val isLiked: Boolean,
)

fun ReviewItem.toUiModel(): ReviewUiModel = ReviewUiModel(
    userId = userId,
    userNickname = userNickname,
    userReview = userReview,
    userTime = userTime,
    reviewLikeNum = reviewLikeNum,
    isLiked = isLiked,
)

fun List<ReviewItem>.toUiModels(): List<ReviewUiModel> = map(ReviewItem::toUiModel)
