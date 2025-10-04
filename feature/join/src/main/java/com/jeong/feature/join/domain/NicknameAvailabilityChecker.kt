package com.jeong.feature.join.domain

interface NicknameAvailabilityChecker {
    suspend fun check(nickname: String): Result<Boolean>
}
