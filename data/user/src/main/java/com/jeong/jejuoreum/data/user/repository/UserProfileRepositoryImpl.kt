package com.jeong.jejuoreum.data.user.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jejuoreum.core.common.firestore.FirestoreConstants
import com.jeong.jejuoreum.data.user.local.PreferenceManager
import com.jeong.jejuoreum.data.user.model.JoinItem
import com.jeong.jejuoreum.domain.user.entity.UserAccount
import com.jeong.jejuoreum.domain.user.repository.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val preferenceManager: PreferenceManager,
) : UserProfileRepository {

    override suspend fun isNicknameAvailable(nickname: String): Result<Boolean> = runCatching {
        val currentUser = firebaseAuth.currentUser
            ?: throw IllegalStateException("User is not authenticated")
        val snapshot = firestore.collection(FirestoreConstants.COLLECTION_USER_INFO)
            .whereEqualTo(FirestoreConstants.FIELD_NICKNAME, nickname)
            .get()
            .await()
        snapshot.documents.all { it.id == currentUser.uid }
    }

    override suspend fun saveNickname(nickname: String): Result<UserAccount> = runCatching {
        val currentUser = firebaseAuth.currentUser
            ?: throw IllegalStateException("User is not authenticated")
        val document = firestore.collection(FirestoreConstants.COLLECTION_USER_INFO)
            .document(currentUser.uid)
        val userInfo = JoinItem(
            uid = currentUser.uid,
            nickname = nickname,
        )
        document.set(userInfo.toMap()).await()
        preferenceManager.setNickname(nickname)
        UserAccount(id = currentUser.uid, nickname = nickname)
    }
}
