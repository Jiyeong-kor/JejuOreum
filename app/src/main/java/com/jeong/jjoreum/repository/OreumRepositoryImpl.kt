package com.jeong.jjoreum.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.data.model.api.OreumRetrofitInterface
import com.jeong.jjoreum.data.model.api.ResultSummary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

@Singleton
class OreumRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val apiService: OreumRetrofitInterface
) : OreumRepository {

    private val _oreumListFlow = MutableStateFlow<List<ResultSummary>>(emptyList())
    override val oreumListFlow: StateFlow<List<ResultSummary>> = _oreumListFlow

    override suspend fun loadOreumListIfNeeded() {
        if (_oreumListFlow.value.isNotEmpty()) return
        _oreumListFlow.value = fetchOreumList()
    }

    override fun getCachedOreumList(): List<ResultSummary> = _oreumListFlow.value

    private suspend fun fetchOreumList(): List<ResultSummary> {
        return try {
            val response = apiService.getOreumList()
            if (response.isSuccessful) {
                val apiData = response.body()?.resultSummary ?: emptyList()
                val enumerated = apiData.mapIndexed { idx, oreum -> oreum.copy(idx = idx) }
                mergeWithFirestoreData(enumerated)
            } else {
                Log.e(
                    "OreumRepository", "❌ API 실패: ${response.errorBody()?.string()}"
                )
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("OreumRepository", "❌ API 예외", e)
            emptyList()
        }
    }

    override suspend fun fetchSingleOreumById(oreumIdx: String): ResultSummary {
        val apiList = fetchOreumList()
        val oreum = apiList.find { it.idx.toString() == oreumIdx }
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

        val userFavorites = userDoc?.get("favorites") as? Map<String, Boolean> ?: emptyMap()
        val userStamps = userDoc?.get("stampedOreums") as? Map<String, String> ?: emptyMap()

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
            // 캐시된 목록이 없으면 그냥 새로 로드
            loadOreumListIfNeeded()
        } else {
            // 캐시된 목록이 있으면 사용자 정보만 덧씌워서 Flow를 업데이트
            _oreumListFlow.value = mergeWithFirestoreData(cachedList)
        }
    }

    private suspend fun mergeWithFirestoreData(apiData: List<ResultSummary>): List<ResultSummary> {
        return try {
            val oreumSnapshot = firestore.collection("oreum_info_col").get().await()
            val userId = auth.currentUser?.uid

            val stampMap =
                oreumSnapshot.documents.associate {
                    it.id to (it.getLong("stamp")?.toInt() ?: 0)
                }
            val favMap = oreumSnapshot.documents.associate {
                it.id to (it.getLong("favorite")?.toInt() ?: 0)
            }

            val userSnapshot = userId?.let {
                firestore.collection(
                    "user_info_col"
                ).document(it).get().await()
            }

            val userFavorites =
                userSnapshot?.get("favorites") as? Map<String, Boolean> ?: emptyMap()
            Log.d("FavoriteDebug", "✅ [읽기 성공] 불러온 즐겨찾기 정보: $userFavorites")

            val userStamps =
                userSnapshot?.get("stampedOreums") as? Map<String, String> ?: emptyMap()

            apiData.map { oreum ->
                val idStr = oreum.idx.toString()
                oreum.copy(
                    totalFavorites = favMap[idStr] ?: 0,
                    totalStamps = stampMap[idStr] ?: 0,
                    userLiked = userFavorites[idStr] == true,
                    userStamped = userStamps.containsKey(idStr)
                )
            }
        } catch (e: Exception) {
            Log.e("OreumRepository", "❌ Firestore 병합 실패", e)
            apiData
        }
    }
}
