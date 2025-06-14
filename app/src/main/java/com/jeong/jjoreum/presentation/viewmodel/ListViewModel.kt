package com.jeong.jjoreum.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.repository.OreumRepository
import com.jeong.jjoreum.repository.StampRepository
import com.jeong.jjoreum.repository.UserInteractionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListViewModel(
    private val oreumRepository: OreumRepository,
    private val userInteractionRepository: UserInteractionRepository,
    private val stampRepository: StampRepository
) : ViewModel() {

    private val _oreumList = MutableStateFlow<List<ResultSummary>>(emptyList())
    val oreumList: StateFlow<List<ResultSummary>> get() = _oreumList

    init {
        loadOreumList()
    }

    fun loadOreumList() {
        viewModelScope.launch {
            oreumRepository.loadOreumListIfNeeded()
            val oreums = oreumRepository.getCachedOreumList()

            val userFavorites = userInteractionRepository.getAllFavoriteStatus()
            val userStamps = userInteractionRepository.getAllStampStatus()

            val updated = oreums.map { oreum ->
                val oreumId = oreum.idx.toString()
                oreum.copy(
                    userLiked = userFavorites[oreumId] ?: false,
                    userStamped = userStamps[oreumId] ?: false
                )
            }

            _oreumList.value = updated.toList()
        }
    }

    fun toggleFavorite(oreumIdx: String) {
        viewModelScope.launch {
            val currentList = _oreumList.value
            val target = currentList.find { it.idx.toString() == oreumIdx } ?: return@launch
            val newIsFavorite = !target.userLiked

            val newTotal = userInteractionRepository.toggleFavorite(oreumIdx, newIsFavorite)

            // 변경된 항목만 반영한 새로운 리스트 생성
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
        oreumLng: Double,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = stampRepository.tryStamp(oreumIdx, oreumName, oreumLat, oreumLng)
            if (result.isSuccess) {
                updateStampStatus()
                onSuccess()
            } else {
                onFailure(result.exceptionOrNull()?.message ?: "알 수 없는 오류")
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

    fun refreshOreumById(oreumIdx: String) {
        viewModelScope.launch {
            val updated = oreumRepository.fetchSingleOreumById(oreumIdx)
            val newList = _oreumList.value.map {
                if (it.idx.toString() == oreumIdx) updated else it
            }
            _oreumList.value = newList.toList()
        }
    }
}
