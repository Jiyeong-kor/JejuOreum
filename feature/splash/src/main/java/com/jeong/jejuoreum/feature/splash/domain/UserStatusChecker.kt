package com.jeong.jejuoreum.feature.splash.domain

interface UserStatusChecker {
    suspend fun isUserRegistered(): Boolean
}
