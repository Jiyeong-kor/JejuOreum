package com.jeong.jejuoreum.feature.profile.presentation.favorite

internal interface MyFavoriteErrorMessageProvider {
    fun favoriteLoadingFailed(throwable: Throwable?): String
}
