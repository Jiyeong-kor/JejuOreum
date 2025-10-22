package com.jeong.jejuoreum.feature.profile.presentation.favorite

import com.jeong.jejuoreum.core.architecture.UiText
import com.jeong.jejuoreum.core.common.result.ResourceError
import com.jeong.jejuoreum.feature.profile.R
import javax.inject.Inject

internal class AndroidMyFavoriteErrorMessageProvider @Inject constructor() :
    MyFavoriteErrorMessageProvider {

    override fun favoriteLoadingFailed(throwable: Throwable?): UiText {
        val detail = throwable?.message?.takeUnless { it.isNullOrBlank() }
        return detail?.let(UiText::DynamicString)
            ?: UiText.StringResource(R.string.profile_favorite_error_generic)
    }

    override fun favoriteStreamFailed(error: ResourceError): UiText {
        val fallback = UiText.StringResource(R.string.profile_favorite_error_generic)
        return when (error) {
            ResourceError.Network -> UiText.StringResource(R.string.profile_favorite_error_network)
            is ResourceError.Api -> error.message?.let(UiText::DynamicString) ?: fallback
            is ResourceError.NotFound -> fallback
            ResourceError.Unauthorized -> fallback
            is ResourceError.Unknown -> error.throwable.message?.let(UiText::DynamicString) ?: fallback
        }
    }
}
