package com.jeong.jjoreum.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.entity.MyStampItem
import com.jeong.jjoreum.repository.OreumRepository
import com.jeong.jjoreum.repository.UserInteractionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyStampViewModel @Inject constructor(
    private val oreumRepository: OreumRepository,
    private val interactionRepository: UserInteractionRepository
) : ViewModel() {

    private val _stampedList = MutableStateFlow<List<MyStampItem>>(emptyList())
    val stampedList: StateFlow<List<MyStampItem>> = _stampedList

    private val _nickname = MutableStateFlow("닉네임없음")
    val nickname: StateFlow<String> = _nickname

    init {
        loadUserNickname()
    }

    fun loadStampedList() {
        viewModelScope.launch {
            oreumRepository.loadOreumListIfNeeded()

            // 캐시된 데이터 직접 사용
            val oreumList = oreumRepository.getCachedOreumList()

            val stamped = oreumList.filter { it.userStamped }.map {
                MyStampItem(
                    userId = 0,
                    oreumIdx = it.idx,
                    oreumName = it.oreumKname,
                    stampBoolean = true
                )
            }
            _stampedList.value = stamped
        }
    }

    private fun loadUserNickname() {
        viewModelScope.launch {
            _nickname.value = interactionRepository.getCurrentUserNickname()
        }
    }
}
