package com.jeong.jjoreum.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.entity.ReviewItem
import com.jeong.jjoreum.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val reviewRepo: ReviewRepository,
    private val auth: FirebaseAuth,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<ReviewItem>>(emptyList())
    val reviews: StateFlow<List<ReviewItem>> = _reviews.asStateFlow()

    private val _oreumIdx = MutableStateFlow("")
    private val _oreumName = MutableStateFlow("")
    val oreumName: StateFlow<String> = _oreumName.asStateFlow()

    private val _reviewInputText = MutableStateFlow("")
    val reviewInputText: StateFlow<String> = _reviewInputText.asStateFlow()

    fun init(oreumIdx: Int, oreumName: String) {
        val idxString = oreumIdx.toString()

        // 중복 초기화 방지
        if (_oreumIdx.value == idxString) return

        _oreumIdx.value = idxString
        _oreumName.value = oreumName
        loadReviews()
    }

    fun onReviewTextChange(text: String) {
        _reviewInputText.value = text
    }

    private fun loadReviews() {
        if (_oreumIdx.value.isEmpty()) return
        viewModelScope.launch {
            _reviews.value = reviewRepo.getReviews(_oreumIdx.value)
        }
    }

    fun saveReview() {
        if (_reviewInputText.value.isBlank()) return

        val currentUser = auth.currentUser ?: return
        val nickname = auth.currentUser?.displayName ?: context.getString(R.string.anonymous)

        val newReview = ReviewItem(
            userId = currentUser.uid,
            userNickname = nickname,
            userReview = _reviewInputText.value.trim(),
            userTime = System.currentTimeMillis()
        )

        viewModelScope.launch {
            reviewRepo.writeReview(_oreumIdx.value, newReview).onSuccess {

                // 입력창 초기화
                _reviewInputText.value = ""

                // 리뷰 목록 새로고침
                loadReviews()
            }
        }
    }

    fun toggleReviewLike(review: ReviewItem) {
        viewModelScope.launch {
            reviewRepo.toggleReviewLike(
                _oreumIdx.value, review.userId
            ).onSuccess {
                loadReviews()
            }
        }
    }

    fun deleteReview(review: ReviewItem) {
        viewModelScope.launch {
            reviewRepo.deleteReview(_oreumIdx.value, review.userId).onSuccess {
                loadReviews()
            }
        }
    }
}
