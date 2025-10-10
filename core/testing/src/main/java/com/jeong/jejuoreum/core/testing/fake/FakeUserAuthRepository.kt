package com.jeong.jejuoreum.core.testing.fake

import com.jeong.jejuoreum.domain.user.entity.UserAccount
import com.jeong.jejuoreum.domain.user.repository.UserAuthRepository

class FakeUserAuthRepository : UserAuthRepository {

    var currentAccount: UserAccount? = null
    var ensureAnonymousUserResult: Result<UserAccount> = Result.success(UserAccount(id = "anonymous"))

    override suspend fun ensureAnonymousUser(): Result<UserAccount> {
        ensureAnonymousUserResult.onSuccess { account -> currentAccount = account }
        return ensureAnonymousUserResult
    }

    override fun currentUserId(): String? = currentAccount?.id
}
