package com.jeong.jejuoreum.data.user.repository

import com.google.firebase.auth.FirebaseAuth
import com.jeong.jejuoreum.domain.user.entity.UserAccount
import com.jeong.jejuoreum.domain.user.repository.UserAuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class UserAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : UserAuthRepository {

    override suspend fun ensureAnonymousUser(): Result<UserAccount> = runCatching {
        val currentUser = firebaseAuth.currentUser ?: firebaseAuth
            .signInAnonymously()
            .await()
            .user
        val user = currentUser ?: throw IllegalStateException("Unable to authenticate user")
        UserAccount(id = user.uid, nickname = user.displayName)
    }

    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid
}
