package com.jeong.jejuoreum.core.testing.fake

import com.jeong.jejuoreum.domain.user.entity.UserAccount
import com.jeong.jejuoreum.domain.user.repository.UserProfileRepository

class FakeUserProfileRepository : UserProfileRepository {

    var isNicknameAvailableResult: Result<Boolean> = Result.success(true)
    var saveNicknameResult: Result<UserAccount> = Result.success(UserAccount(id = "test", nickname = "tester"))

    var lastCheckedNickname: String? = null
    var lastSavedNickname: String? = null

    override suspend fun isNicknameAvailable(nickname: String): Result<Boolean> {
        lastCheckedNickname = nickname
        return isNicknameAvailableResult
    }

    override suspend fun saveNickname(nickname: String): Result<UserAccount> {
        lastSavedNickname = nickname
        return saveNicknameResult
    }
}
