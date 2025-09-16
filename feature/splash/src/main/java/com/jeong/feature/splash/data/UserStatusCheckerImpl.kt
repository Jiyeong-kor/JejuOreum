package com.jeong.feature.splash.data

import com.jeong.data.local.PreferenceManager
import com.jeong.feature.splash.domain.UserStatusChecker
import javax.inject.Inject

class UserStatusCheckerImpl @Inject constructor(
    private val prefs: PreferenceManager
) : UserStatusChecker {
    override suspend fun isUserRegistered(): Boolean = prefs.isUserRegistered()
}
