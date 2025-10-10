package com.jeong.jejuoreum.feature.detail.presentation.review

data class WriteReviewUiState(
    val oreumIdx: String? = null,
    val oreumName: String = "",
    val reviews: List<ReviewUiModel> = emptyList(),
    val reviewInput: String = "",
    val currentUserId: String? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
)

sealed interface WriteReviewUiEvent {
    data class Initialize(val oreumIdx: Int, val oreumName: String) : WriteReviewUiEvent
    data class ReviewTextChanged(val text: String) : WriteReviewUiEvent
    data class SaveClicked(val defaultNickname: String) : WriteReviewUiEvent
    data class LikeClicked(val review: ReviewUiModel) : WriteReviewUiEvent
    data class DeleteClicked(val review: ReviewUiModel) : WriteReviewUiEvent
    data object RefreshRequested : WriteReviewUiEvent
}

sealed interface WriteReviewUiEffect {
    data class ShowMessage(val message: String) : WriteReviewUiEffect
}
