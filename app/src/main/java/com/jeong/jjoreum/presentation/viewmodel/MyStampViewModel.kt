package com.jeong.jjoreum.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.data.model.entity.MyStampItem
import com.jeong.jjoreum.repository.OreumRepository
import com.jeong.jjoreum.repository.UserInteractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyStampViewModel @Inject constructor(
    private val oreumRepository: OreumRepository,
    private val interactionRepository: UserInteractionRepository
) : ViewModel() {

    private val _stampedList = MutableStateFlow<List<MyStampItem>>(emptyList())
    val stampedList: StateFlow<List<MyStampItem>> = _stampedList

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    init {
        loadUserNickname()
    }

    fun loadStampedList() {
        viewModelScope.launch {
            val result = oreumRepository.loadOreumListIfNeeded()
            if (result.isFailure) return@launch
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
