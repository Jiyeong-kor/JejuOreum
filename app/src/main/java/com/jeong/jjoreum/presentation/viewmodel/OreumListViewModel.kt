package com.jeong.jjoreum.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.repository.OreumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 오름 리스트를 가져와서 StateFlow로 관리하는 ViewModel
 * @param repository 오름 데이터를 가져오는 Repository
 */
class OreumListViewModel(private val repository: OreumRepository) : ViewModel() {

    // 오름 목록을 담는 StateFlow
    private val _oreumList = MutableStateFlow<List<ResultSummary>>(emptyList())
    val oreumList: StateFlow<List<ResultSummary>> = _oreumList.asStateFlow()

    /**
     * 오름 리스트를 비동기로 가져와서 Flow에 반영
     */
    fun fetchOreumList() {
        viewModelScope.launch {
            val result = repository.getOreumList()
            _oreumList.value = result
        }
    }

    /**
     * 특정 오름의 좋아요 개수와 isFavorite 상태를 업데이트.
     * @param oreumIdx 오름 idx (mapIndexed로 할당된 int)
     * @param newFavoriteCount 업데이트된 좋아요 개수
     * @param newIsFavorite 좋아요 여부
     */
    fun updateFavorite(oreumIdx: Int, newFavoriteCount: Int) {
        val oldList = _oreumList.value
        val newList = oldList.map { oreum ->
            if (oreum.idx == oreumIdx) {
                oreum.copy(favorite = newFavoriteCount)
            } else {
                oreum
            }
        }
        _oreumList.value = newList
    }

}
