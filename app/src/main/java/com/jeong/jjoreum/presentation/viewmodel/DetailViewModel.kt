package com.jeong.jjoreum.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.data.model.entity.ReviewItem
import com.jeong.jjoreum.repository.ReviewRepository
import com.jeong.jjoreum.repository.StampRepository
import com.jeong.jjoreum.repository.UserInteractionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val interactionRepo: UserInteractionRepository,
    private val reviewRepo: ReviewRepository,
    private val stampRepo: StampRepository
) : ViewModel() {

    private val _oreumDetail = MutableStateFlow<ResultSummary?>(null)
    val oreumDetail: StateFlow<ResultSummary?> get() = _oreumDetail

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> get() = _isFavorite

    private val _hasStamp = MutableStateFlow(false)
    val hasStamp: StateFlow<Boolean> get() = _hasStamp

    private val _reviewList = MutableStateFlow<List<ReviewItem>>(emptyList())
    val reviewList: StateFlow<List<ReviewItem>> get() = _reviewList

    private val _event = MutableStateFlow<DetailEvent?>(null)
    val event: StateFlow<DetailEvent?> get() = _event

    fun setOreumDetail(oreum: ResultSummary) {
        _oreumDetail.value = oreum
        _isFavorite.value = oreum.userLiked
        _hasStamp.value = oreum.userStamped
    }

    fun toggleFavorite(oreumIdx: String) {
        viewModelScope.launch {
            val newStatus = !_isFavorite.value
            interactionRepo.toggleFavorite(oreumIdx, newStatus)
            _isFavorite.value = newStatus
        }
    }

    fun loadReviews(oreumIdx: String) {
        viewModelScope.launch {
            _reviewList.value = reviewRepo.getReviews(oreumIdx)
        }
    }

    fun stampOreum(oreumIdx: String, oreumName: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            val result = stampRepo.tryStamp(oreumIdx, oreumName, lat, lng)
            result.onSuccess {
                _hasStamp.value = true
                _event.value = DetailEvent.StampSuccess
            }.onFailure {
                _event.value = DetailEvent.StampFailure(it.message ?: "스탬프 실패")
            }
        }
    }

    fun clearEvent() {
        _event.value = null
    }

    sealed class DetailEvent {
        data object StampSuccess : DetailEvent()
        data class StampFailure(val message: String) : DetailEvent()
    }
}
