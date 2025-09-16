package com.jeong.feature.splash.domain

interface UserStatusChecker {
    suspend fun isUserRegistered(): Boolean
}
