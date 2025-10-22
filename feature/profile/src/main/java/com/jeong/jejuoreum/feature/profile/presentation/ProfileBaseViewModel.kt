package com.jeong.jejuoreum.feature.profile.presentation

import com.jeong.jejuoreum.core.architecture.UiText
import com.jeong.jejuoreum.core.architecture.viewmodel.CommonBaseViewModel
import com.jeong.jejuoreum.feature.profile.presentation.favorite.MyFavoriteUiEffect
import com.jeong.jejuoreum.feature.profile.presentation.favorite.MyFavoriteUiEvent
import com.jeong.jejuoreum.feature.profile.presentation.favorite.MyFavoriteUiState
import kotlinx.coroutines.CoroutineDispatcher

/**
 * 프로필 기능 전반에서 공통으로 사용하는 ViewModel 상위 클래스
 * 즐겨찾기 화면에서 발생하는 오류 메시지를 일관되게 노출하기 위해 효과 변환을 중앙에서 처리
 */
public abstract class ProfileBaseViewModel(
    ioDispatcher: CoroutineDispatcher,
) : CommonBaseViewModel<MyFavoriteUiState, MyFavoriteUiEvent, MyFavoriteUiEffect>(ioDispatcher) {

    override fun buildErrorEffect(message: UiText): MyFavoriteUiEffect =
        MyFavoriteUiEffect.ShowError(message)
}
