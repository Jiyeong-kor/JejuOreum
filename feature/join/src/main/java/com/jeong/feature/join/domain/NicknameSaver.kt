package com.jeong.feature.join.domain

import com.jeong.domain.entity.UserAccount

interface NicknameSaver {
    suspend fun save(nickname: String): Result<UserAccount>
}
