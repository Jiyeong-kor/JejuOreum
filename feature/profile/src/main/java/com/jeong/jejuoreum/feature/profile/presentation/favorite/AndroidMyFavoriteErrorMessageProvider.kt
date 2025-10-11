package com.jeong.jejuoreum.feature.profile.presentation.favorite

import android.content.Context
import com.jeong.jejuoreum.core.common.result.ResourceError
import com.jeong.jejuoreum.feature.profile.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class AndroidMyFavoriteErrorMessageProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : MyFavoriteErrorMessageProvider {

    override fun favoriteLoadingFailed(throwable: Throwable?): String {
        val fallback = context.getString(R.string.profile_favorite_error_generic)
        val detail = throwable?.message?.takeUnless { it.isNullOrBlank() }
        return detail ?: fallback
    }

    override fun favoriteStreamFailed(error: ResourceError): String {
        val fallback = context.getString(R.string.profile_favorite_error_generic)
        return when (error) {
            ResourceError.Network -> context.getString(R.string.profile_favorite_error_generic)
            is ResourceError.Api -> error.message ?: fallback
            is ResourceError.NotFound -> fallback
            ResourceError.Unauthorized -> fallback
            is ResourceError.Unknown -> error.throwable.message ?: fallback
        }
    }
}
