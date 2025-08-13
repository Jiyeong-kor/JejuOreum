package com.jeong.jjoreum.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.entity.ReviewItem
import com.jeong.jjoreum.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val reviewRepo: ReviewRepository
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<ReviewItem>>(emptyList())
    val reviews: StateFlow<List<ReviewItem>> = _reviews

    fun loadReviews(oreumIdx: String) {
        viewModelScope.launch {
            try {
                val list = reviewRepo.getReviews(oreumIdx)
                _reviews.value = list
            } catch (e: Exception) {
                // 필요 시 오류 처리
            }
        }
    }

    fun saveReview(
        oreumIdx: String,
        review: ReviewItem,
        onResult: (success: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = reviewRepo.writeReview(oreumIdx, review)
            if (result.isSuccess) {
                loadReviews(oreumIdx)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun toggleReviewLike(oreumIdx: String, userId: String) {
        viewModelScope.launch {
            val result = reviewRepo.toggleReviewLike(oreumIdx, userId)
            if (result.isSuccess) {
                loadReviews(oreumIdx)
            }
        }
    }

    fun deleteReview(
        oreumIdx: String,
        userId: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = reviewRepo.deleteReview(oreumIdx, userId)
            if (result.isSuccess) {
                loadReviews(oreumIdx)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}