package com.jeong.jjoreum.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.repository.OreumRepository
import com.jeong.jjoreum.repository.UserInteractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFavoriteViewModel @Inject constructor(
    private val oreumRepository: OreumRepository,
    private val userInteractionRepo: UserInteractionRepository
) : ViewModel() {

    val favoriteList: StateFlow<List<ResultSummary>> =
        oreumRepository.oreumListFlow
            .map { list -> list.filter { it.userLiked } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        viewModelScope.launch {
            oreumRepository.loadOreumListIfNeeded()
        }
    }

    fun toggleFavorite(oreumIdx: String, newStatus: Boolean) {
        viewModelScope.launch {
            userInteractionRepo.toggleFavorite(oreumIdx, newStatus)
            oreumRepository.refreshAllOreumsWithNewUserData()
        }
    }
}
