package com.jeong.jjoreum.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.jeong.jjoreum.data.model.api.OreumRetrofitInterface
import com.jeong.jjoreum.data.model.api.ResultSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OreumRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val apiService: OreumRetrofitInterface
) : OreumRepository {

    private val _oreumListFlow = MutableStateFlow<List<ResultSummary>>(emptyList())
    override val oreumListFlow: StateFlow<List<ResultSummary>> = _oreumListFlow

    override suspend fun loadOreumListIfNeeded(): Result<Unit> {
        if (_oreumListFlow.value.isNotEmpty()) return Result.success(Unit)
        return fetchOreumList()
            .onSuccess { _oreumListFlow.value = it }
            .map { }
    }

    override fun getCachedOreumList(): List<ResultSummary> = _oreumListFlow.value

    private suspend fun fetchOreumList(): Result<List<ResultSummary>> {
        return runCatching {
            val response = apiService.getOreumList()
            if (!response.isSuccessful) {
                throw IllegalStateException("API 실패: ${response.errorBody()?.string()}")
            }
            val apiData = response.body()?.resultSummary ?: emptyList()
            val enumerated = apiData.mapIndexed { idx, oreum -> oreum.copy(idx = idx) }
            mergeWithFirestoreData(enumerated)
                .onFailure { Log.e("OreumRepository", "❌ Firestore 병합 실패", it) }
                .getOrThrow()
        }
    }

    override suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary {
        loadOreumListIfNeeded().getOrThrow()
        val oreum = _oreumListFlow.value.find { it.idx.toString() == oreumIdx }
            ?: throw IllegalStateException("해당 오름을 찾을 수 없습니다.")

        val userId = auth.currentUser?.uid
        val oreumDoc = firestore.collection(
            "oreum_info_col"
        ).document(oreumIdx).get().await()
        val userDoc = userId?.let {
            firestore.collection(
                "user_info_col"
            ).document(it).get().await()
        }

        val totalFavorites = oreumDoc.getLong("favorite")?.toInt() ?: 0
        val totalStamps = oreumDoc.getLong("stamp")?.toInt() ?: 0

        val userFavorites = userDoc?.get("favorites").toStringBooleanMap()
        val userStamps = userDoc?.get("stampedOreums").toStringStringMap()

        val userLiked = userFavorites[oreumIdx] == true
        val userStamped = userStamps.containsKey(oreumIdx)

        return oreum.copy(
            totalFavorites = totalFavorites,
            totalStamps = totalStamps,
            userLiked = userLiked,
            userStamped = userStamped
        )
    }

    override suspend fun refreshAllOreumsWithNewUserData() {
        val cachedList = getCachedOreumList()

        if (cachedList.isEmpty()) {
            fetchOreumList().onSuccess { _oreumListFlow.value = it }
        } else {
            mergeWithFirestoreData(cachedList)
                .onSuccess { _oreumListFlow.value = it }
                .onFailure { Log.e("OreumRepository", "❌ Firestore 병합 실패", it) }
                .getOrThrow()
        }
    }

    private data class OreumFirestoreData(
        val favoriteCounts: Map<String, Int>,
        val stampCounts: Map<String, Int>
    )

    private data class UserFirestoreData(
        val favorites: Map<String, Boolean>,
        val stampedOreums: Map<String, String>
    )

    private suspend fun fetchOreumFirestoreData(): OreumFirestoreData {
        val oreumSnapshot = firestore.collection("oreum_info_col").get().await()
        val stampMap = oreumSnapshot.documents.associate {
            it.id to (it.getLong("stamp")?.toInt() ?: 0)
        }
        val favMap = oreumSnapshot.documents.associate {
            it.id to (it.getLong("favorite")?.toInt() ?: 0)
        }
        return OreumFirestoreData(favMap, stampMap)
    }

    private suspend fun fetchUserFirestoreData(userId: String?): UserFirestoreData {
        if (userId == null) return UserFirestoreData(emptyMap(), emptyMap())
        val userSnapshot = firestore.collection("user_info_col")
            .document(userId).get(Source.SERVER).await()
        val userFavorites = userSnapshot.get("favorites").toStringBooleanMap()
        val userStamps = userSnapshot.get("stampedOreums").toStringStringMap()
        return UserFirestoreData(userFavorites, userStamps)
    }

    private fun applyFirestoreData(
        apiData: List<ResultSummary>,
        oreumData: OreumFirestoreData,
        userData: UserFirestoreData
    ): List<ResultSummary> {
        return apiData.map { oreum ->
            val idStr = oreum.idx.toString()
            oreum.copy(
                totalFavorites = oreumData.favoriteCounts[idStr] ?: 0,
                totalStamps = oreumData.stampCounts[idStr] ?: 0,
                userLiked = userData.favorites[idStr] == true,
                userStamped = userData.stampedOreums.containsKey(idStr)
            )
        }
    }

    private suspend fun mergeWithFirestoreData(
        apiData: List<ResultSummary>
    ): Result<List<ResultSummary>> {
        return runCatching {
            val oreumData = fetchOreumFirestoreData()
            val userData = fetchUserFirestoreData(auth.currentUser?.uid)
            applyFirestoreData(apiData, oreumData, userData)
        }
    }
}
