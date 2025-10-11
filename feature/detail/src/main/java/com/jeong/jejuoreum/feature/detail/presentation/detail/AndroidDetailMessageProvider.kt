package com.jeong.jejuoreum.feature.detail.presentation.detail

import android.content.Context
import com.jeong.jejuoreum.feature.detail.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidDetailMessageProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : DetailMessageProvider {

    override fun defaultError(): String =
        context.getString(R.string.detail_error_generic)

    override fun networkError(): String =
        context.getString(R.string.detail_error_network)

    override fun reviewNotFound(): String =
        context.getString(R.string.detail_error_review_not_found)
