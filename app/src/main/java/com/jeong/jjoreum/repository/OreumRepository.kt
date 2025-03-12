package com.jeong.jjoreum.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jjoreum.data.model.api.OreumRetrofitInterface
import com.jeong.jjoreum.data.model.api.ResultSummary
import kotlinx.coroutines.tasks.await

class OreumRepository(private val apiService: OreumRetrofitInterface) {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getOreumList(): List<ResultSummary> {
        return try {
            val response = apiService.getOreumList()
            if (response.isSuccessful) {
                // API에서 받아온 오름 목록
                val apiData = response.body()?.resultSummary ?: emptyList()

                // ★ 여기서 mapIndexed로 각 오름에 인덱스를 부여 ★
                val enumeratedApiData = apiData.mapIndexed { index, oreum ->
                    oreum.copy(idx = index) // idx 필드에 index 값을 넣는다
                }

                // Firestore 데이터와 병합
                mergeWithFirestoreData(enumeratedApiData)
            } else {
                Log.e("OreumRepository", "❌ API 호출 실패: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("OreumRepository", "❌ 오름 목록 가져오기 실패", e)
            emptyList()
        }
    }

    private suspend fun mergeWithFirestoreData(apiData: List<ResultSummary>): List<ResultSummary> {
        return try {
            val firestoreSnapshot = firestore.collection("oreum_info_col").get().await()
            val firestoreStampData: Map<String, Int> = firestoreSnapshot.documents.associate {
                it.id to (it.getLong("stamp")?.toInt() ?: 0)
            }
            val firestoreFavoriteData: Map<String, Int> = firestoreSnapshot.documents.associate {
                it.id to (it.getLong("favorite")?.toInt() ?: 0)
            }

            apiData.map { oreum ->
                val oreumId = oreum.idx.toString()
                val stampCount = firestoreStampData[oreumId] ?: 0
                val favoriteCount = firestoreFavoriteData[oreumId] ?: 0

                oreum.copy(stamp = stampCount, favorite = favoriteCount)
            }
        } catch (e: Exception) {
            Log.e("OreumRepository", "❌ Firestore 데이터 병합 실패", e)
            apiData
        }
    }


}
