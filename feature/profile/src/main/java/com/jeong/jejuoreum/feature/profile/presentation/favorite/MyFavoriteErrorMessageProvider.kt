package com.jeong.jejuoreum.feature.profile.presentation.favorite

import com.jeong.jejuoreum.core.common.UiText
import com.jeong.jejuoreum.core.common.result.ResourceError

internal interface MyFavoriteErrorMessageProvider {
    fun favoriteLoadingFailed(throwable: Throwable?): UiText
    fun favoriteStreamFailed(error: ResourceError): UiText
}
