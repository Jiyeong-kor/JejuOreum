package com.jeong.jejuoreum.feature.detail.presentation.detail

/**
 * Provides user-facing error messages for the Detail feature.
 */
import com.jeong.jejuoreum.core.architecture.UiText

interface DetailMessageProvider {
    fun defaultError(): UiText
    fun networkError(): UiText
    fun reviewNotFound(): UiText
    fun loginRequired(): UiText
}
