package com.jeong.jejuoreum.feature.profile.presentation.favorite

import com.jeong.jejuoreum.core.common.result.ResourceError

internal interface MyFavoriteErrorMessageProvider {
    fun favoriteLoadingFailed(throwable: Throwable?): String
    fun favoriteStreamFailed(error: ResourceError): String
}
