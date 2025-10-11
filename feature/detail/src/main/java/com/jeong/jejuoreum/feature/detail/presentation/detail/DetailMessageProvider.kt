package com.jeong.jejuoreum.feature.detail.presentation.detail

/**
 * Provides user-facing error messages for the Detail feature.
 */
interface DetailMessageProvider {
    fun defaultError(): String
    fun networkError(): String
    fun reviewNotFound(): String
    fun loginRequired(): String
}
