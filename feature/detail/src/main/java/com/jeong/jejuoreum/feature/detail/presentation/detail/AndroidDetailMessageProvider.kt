package com.jeong.jejuoreum.feature.detail.presentation.detail

import com.jeong.jejuoreum.core.architecture.UiText
import com.jeong.jejuoreum.feature.detail.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidDetailMessageProvider @Inject constructor() : DetailMessageProvider {

    override fun defaultError(): UiText =
        UiText.StringResource(R.string.detail_error_generic)

    override fun networkError(): UiText =
        UiText.StringResource(R.string.detail_error_network)

    override fun reviewNotFound(): UiText =
        UiText.StringResource(R.string.detail_error_review_not_found)

    override fun loginRequired(): UiText =
        UiText.StringResource(R.string.detail_error_login_required)
}
