package com.jeong.jjoreum.presentation.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.data.model.entity.ReviewItem
import com.jeong.jjoreum.repository.ReviewRepository
import com.jeong.jjoreum.repository.StampRepository
import com.jeong.jjoreum.repository.UserInteractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository,
    private val reviewRepository: ReviewRepository,
    private val stampRepository: StampRepository
) : ViewModel() {

    private val _oreumDetail = MutableStateFlow<ResultSummary?>(null)
    val oreumDetail: StateFlow<ResultSummary?> = _oreumDetail.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _hasStamp = MutableStateFlow(false)
    val hasStamp: StateFlow<Boolean> = _hasStamp.asStateFlow()

    private val _reviewList =
        MutableStateFlow<List<ReviewItem>>(emptyList())
    val reviewList: StateFlow<List<ReviewItem>> =
        _reviewList.asStateFlow()

    sealed class DetailEvent {
        object StampSuccess : DetailEvent()
        data class StampFailure(val message: String) : DetailEvent()
    }

    private val _event = MutableStateFlow<DetailEvent?>(null)
    val event: StateFlow<DetailEvent?> = _event.asStateFlow()

    fun setOreumDetail(oreum: ResultSummary) {
        _oreumDetail.value = oreum
        fetchFavoriteStatus(oreum.idx.toString())
        fetchStampStatus()
        loadReviews(oreum.idx.toString())
    }

    private fun fetchFavoriteStatus(oreumIdx: String) {
        viewModelScope.launch {
            val isLiked = userInteractionRepository.getFavoriteStatus(oreumIdx)
            _isFavorite.value = isLiked
        }
    }

    private fun fetchStampStatus() {
        viewModelScope.launch {
            _oreumDetail.value?.idx?.toString()?.let {
                val hasStamped = userInteractionRepository.getStampStatus(it)
                _hasStamp.value = hasStamped
            }
        }
    }

    fun toggleFavorite(oreumIdx: String) {
        viewModelScope.launch {
            val newIsFavorite = !_isFavorite.value
            userInteractionRepository.toggleFavorite(oreumIdx, newIsFavorite)
            _isFavorite.value = newIsFavorite
        }
    }

    fun loadReviews(oreumIdx: String) {
        viewModelScope.launch {
            val reviews = reviewRepository.getReviews(oreumIdx)
            _reviewList.value = reviews
        }
    }

    fun stampOreum() {
        viewModelScope.launch {
            val oreum = _oreumDetail.value ?: return@launch

            val result = stampRepository.tryStamp(
                oreumIdx = oreum.idx.toString(),
                oreumName = oreum.oreumKname,
                oreumLat = oreum.y,
                oreumLng = oreum.x
            )

            _event.value = when {
                result.isSuccess -> {
                    fetchStampStatus()
                    DetailEvent.StampSuccess
                }

                result.isFailure -> {
                    val message = result.exceptionOrNull()?.message ?: "알 수 없는 오류"
                    DetailEvent.StampFailure(message)
                }

                else -> null
            }
        }
    }

    fun clearEvent() {
        _event.value = null
    }
}