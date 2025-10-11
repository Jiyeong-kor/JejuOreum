package com.jeong.jejuoreum.feature.profile.presentation

import com.jeong.jejuoreum.core.presentation.CommonBaseViewModel
import com.jeong.jejuoreum.feature.profile.presentation.favorite.MyFavoriteUiEffect
import com.jeong.jejuoreum.feature.profile.presentation.favorite.MyFavoriteUiEvent
import com.jeong.jejuoreum.feature.profile.presentation.favorite.MyFavoriteUiState
import kotlinx.coroutines.CoroutineDispatcher

public abstract class ProfileBaseViewModel(
    ioDispatcher: CoroutineDispatcher,
) : CommonBaseViewModel<MyFavoriteUiState, MyFavoriteUiEvent, MyFavoriteUiEffect>(ioDispatcher) {

    override fun createErrorEffect(message: String): MyFavoriteUiEffect =
        MyFavoriteUiEffect.ShowError(message)
}
