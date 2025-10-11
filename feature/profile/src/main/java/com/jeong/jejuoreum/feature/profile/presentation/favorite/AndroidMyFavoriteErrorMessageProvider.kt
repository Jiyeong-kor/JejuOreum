package com.jeong.jejuoreum.feature.profile.presentation.favorite

import android.content.Context
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
}
