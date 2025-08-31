package com.jeong.jjoreum.presentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.repository.OreumRepository
import com.jeong.jjoreum.repository.StampRepository
import com.jeong.jjoreum.repository.UserInteractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val oreumRepository: OreumRepository,
    private val userInteractionRepository: UserInteractionRepository,
    private val stampRepository: StampRepository
) : ViewModel() {
    private val _oreumList = MutableStateFlow<List<ResultSummary>>(emptyList())
    val oreumList: StateFlow<List<ResultSummary>> get() = _oreumList

    private val _stampResult = MutableStateFlow<Result<Unit>?>(null)
    val stampResult: StateFlow<Result<Unit>?> = _stampResult.asStateFlow()

    init {
        viewModelScope.launch {
            oreumRepository.loadOreumListIfNeeded()
            oreumRepository.oreumListFlow.collect { oreums ->
                _oreumList.value = oreums
            }
        }
    }

    fun toggleFavorite(oreumIdx: String) {
        viewModelScope.launch {
            val currentList = _oreumList.value
            val target = currentList.find { it.idx.toString() == oreumIdx } ?: return@launch
            val newIsFavorite = !target.userLiked
            val newTotal = userInteractionRepository.toggleFavorite(oreumIdx, newIsFavorite)

            oreumRepository.refreshAllOreumsWithNewUserData()

            val updatedList = currentList.map {
                if (it.idx.toString() == oreumIdx) {
                    it.copy(userLiked = newIsFavorite, totalFavorites = newTotal)
                } else {
                    it
                }
            }
            _oreumList.value = updatedList.toList()
        }
    }

    fun tryStamp(
        oreumIdx: String,
        oreumName: String,
        oreumLat: Double,
        oreumLng: Double
    ) {
        viewModelScope.launch {
            val result = stampRepository.tryStamp(oreumIdx, oreumName, oreumLat, oreumLng)
            _stampResult.value = result
            if (result.isSuccess) {
                updateStampStatus()
            }
        }
    }

    private fun updateStampStatus() {
        viewModelScope.launch {
            val userStamps = userInteractionRepository.getAllStampStatus()
            val updated = _oreumList.value.map {
                val oreumId = it.idx.toString()
                it.copy(userStamped = userStamps[oreumId] ?: false)
            }
            _oreumList.value = updated
        }
    }

    fun clearStampResult() {
        _stampResult.value = null
    }
}