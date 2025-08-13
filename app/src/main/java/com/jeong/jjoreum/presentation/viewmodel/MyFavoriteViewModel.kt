package com.jeong.jjoreum.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.repository.OreumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyFavoriteViewModel @Inject constructor(
    private val oreumRepository: OreumRepository
) : ViewModel() {

    private val _favoriteList = MutableStateFlow<List<ResultSummary>>(emptyList())
    val favoriteList: StateFlow<List<ResultSummary>> get() = _favoriteList

    init {
        viewModelScope.launch {
            oreumRepository.loadOreumListIfNeeded()
            oreumRepository.oreumListFlow
                .map { list -> list.filter { it.userLiked } }
                .collect { filteredList ->
                    _favoriteList.value = filteredList
                }
        }
    }
}
