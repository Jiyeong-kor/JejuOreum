package com.jeong.jjoreum.presentation.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jeong.jjoreum.data.model.api.ResultSummary

/**
 * 상세 화면에서 오름 데이터와 스탬프, 즐겨찾기 등을 관리하는 ViewModel
 */
class DetailViewModel : ViewModel() {

    // Firestore 인스턴스
    private val db = FirebaseFirestore.getInstance()

    // 오름 상세 정보 LiveData
    private val _oreumDetail = MutableLiveData<ResultSummary>()
    val oreumDetail: LiveData<ResultSummary> get() = _oreumDetail

    // 즐겨찾기 상태 LiveData
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    // 스탬프 획득 여부 LiveData
    private val _hasStamp = MutableLiveData<Boolean>()
    val hasStamp: LiveData<Boolean> get() = _hasStamp

    /**
     * 오름 상세 정보를 설정하는 함수
     * @param oreum 상세 정보가 담긴 ResultSummary 객체
     */
    fun setOreumDetail(oreum: ResultSummary) {
        _oreumDetail.value = oreum
    }

    /**
     * 사용자의 스탬프 획득 상태를 Firestore에서 조회하는 함수
     * @param nickname 사용자 닉네임
     * @param oreumIdx 오름 식별자
     */
    fun fetchStampStatus(nickname: String, oreumIdx: String) {
        val userDocRef = db.collection("user_info_col").document(nickname)
        userDocRef.get().addOnSuccessListener { document ->
            val stampedOreums = document.get("stampedOreums") as? Map<String, String>
            _hasStamp.value = stampedOreums?.containsKey(oreumIdx) ?: false
        }.addOnFailureListener {
            _hasStamp.value = false
        }
    }

    /**
     * 파이어스토어에서 현재 오름의 좋아요 상태 가져오기
//     */
//    fun fetchFavoriteStatus(nickname: String, oreumIdx: String) {
//        val userDocRef = db.collection("user_info_col").document(nickname)
//        userDocRef.get().addOnSuccessListener { document ->
//            val favorites = document.get("favorites") as? Map<String, Boolean>
//            _isFavorite.value = favorites?.get(oreumIdx) ?: false
//        }.addOnFailureListener {
//            _isFavorite.value = false
//        }
//    }

    /**
     * 사용자가 오름 근처에 있는지 확인하고, 가까우면 스탬프를 Firestore에 저장하는 함수
     * @param context 컨텍스트
     * @param fusedLocationClient 위치 정보 제공자
     * @param oreumIdx 오름 식별자
     * @param oreum 오름 정보 객체
     * @param nickname 사용자 닉네임
     * @param onSuccess 스탬프 저장 성공 시 콜백
     * @param onFailure 스탬프 저장 실패 시 콜백
     */
    fun checkUserLocation(
        context: Context,
        fusedLocationClient: FusedLocationProviderClient,
        oreumIdx: String,
        oreum: ResultSummary,
        nickname: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onFailure("위치 권한이 필요합니다.")
            return
        }

        // 마지막으로 저장된 위치 정보 가져오기
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location == null) {
                Log.e("LocationCheck", "❌ 현재 위치 정보를 가져올 수 없습니다.")
                onFailure("현재 위치를 가져올 수 없습니다.")
                return@addOnSuccessListener
            }

            Log.d("LocationCheck", "🟢 사용자 위치: ${location.latitude}, ${location.longitude}")
            Log.d("LocationCheck", "🟢 오름 위치: ${oreum.y}, ${oreum.x}")

            val oreumLocation = Location("").apply {
                latitude = oreum.y  // 오름의 위도
                longitude = oreum.x // 오름의 경도
            }
            val distance = location.distanceTo(oreumLocation)

            Log.d("LocationCheck", "🟢 계산된 거리: $distance m")

            // 일정 거리(5km) 이내에 있어야 스탬프를 찍을 수 있음
            if (distance <= 5000) {
                saveStampToFirestore(nickname, oreumIdx, oreum.oreumKname, onSuccess, onFailure)
            } else {
                Log.d("LocationCheck", "❌ 거리 초과: $distance m")
                onFailure("오름과의 거리가 5km 이내여야 스탬프를 찍을 수 있습니다. 현재 거리: ${"%.2f".format(distance)}m")
            }
        }.addOnFailureListener {
            Log.e("LocationCheck", "❌ 위치 가져오기 실패: ${it.message}")
            onFailure("현재 위치를 가져오는 데 실패했습니다.")
        }
    }

    /**
     * Firestore에 스탬프 정보를 저장하는 함수
     * @param nickname 사용자 닉네임
     * @param oreumIdx 오름 식별자
     * @param oreumName 오름 이름
     * @param onSuccess 성공 시 콜백
     * @param onFailure 실패 시 콜백
     */
    fun saveStampToFirestore(
        nickname: String,
        oreumIdx: String,
        oreumName: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // 닉네임으로 문서 조회
        db.collection("user_info_col")
            .whereEqualTo("nickname", nickname)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.e("Firestore", "❌ 유저 ID를 찾을 수 없음: $nickname")
                    onFailure("유저 정보를 찾을 수 없습니다.")
                    return@addOnSuccessListener
                }

                // 문서 ID 가져오기
                val userId = documents.documents[0].id
                Log.d("Firestore", "🟢 유저 ID 조회 성공: $userId")

                val userDocRef = db.collection("user_info_col").document(userId)
                val oreumDocRef = db.collection("oreum_info_col").document(oreumIdx)

                // user_info_col 컬렉션에서 사용자 문서를 확인
                userDocRef.get().addOnSuccessListener { documentSnapshot ->
                    if (!documentSnapshot.exists()) {
                        // 문서가 없으면 기본 데이터 생성
                        val newUserData = mapOf(
                            "id" to userId,
                            "nickname" to nickname,
                            "stampedOreums" to mapOf<String, String>()
                        )
                        userDocRef.set(newUserData).addOnSuccessListener {
                            Log.d("Firestore", "✅ 유저 문서 생성 완료: $userId")
                            updateStampData(userDocRef, oreumDocRef, oreumIdx, oreumName, onSuccess, onFailure)
                        }.addOnFailureListener { e ->
                            Log.e("Firestore", "❌ 유저 문서 생성 실패: ${e.message}")
                            onFailure("유저 정보를 생성하는 데 실패했습니다.")
                        }
                    } else {
                        // 문서가 존재하면 바로 업데이트 진행
                        updateStampData(userDocRef, oreumDocRef, oreumIdx, oreumName, onSuccess, onFailure)
                    }
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "❌ Firestore 문서 확인 실패: ${e.message}")
                    onFailure("Firestore 문서 확인에 실패했습니다.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ 유저 ID 조회 실패: ${e.message}")
                onFailure("유저 정보를 가져오는 데 실패했습니다.")
            }
    }

    /**
     * 사용자 문서와 오름 문서를 업데이트하여 스탬프 정보를 저장하는 함수
     * @param userDocRef 사용자 문서 참조
     * @param oreumDocRef 오름 문서 참조
     * @param oreumIdx 오름 식별자
     * @param oreumName 오름 이름
     * @param onSuccess 성공 시 콜백
     * @param onFailure 실패 시 콜백
     */
    private fun updateStampData(
        userDocRef: DocumentReference,
        oreumDocRef: DocumentReference,
        oreumIdx: String,
        oreumName: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userDocRef)
            val oreumSnapshot = transaction.get(oreumDocRef)

            val stampedOreums = userSnapshot.get("stampedOreums") as? MutableMap<String, String> ?: mutableMapOf()

            if (stampedOreums.containsKey(oreumIdx)) {
                Log.e("Firestore", "❌ 이미 스탬프를 찍은 오름입니다! [오름 ID: $oreumIdx]")
                throw Exception("이미 스탬프를 찍은 오름입니다.")
            }

            stampedOreums[oreumIdx] = oreumName
            transaction.set(userDocRef, mapOf("stampedOreums" to stampedOreums), SetOptions.merge())

            val currentStampCount = oreumSnapshot.getLong("stamp") ?: 0
            transaction.set(oreumDocRef, mapOf("stamp" to (currentStampCount + 1)), SetOptions.merge())
        }.addOnSuccessListener {
            Log.d("Firestore", "✅ 스탬프가 성공적으로 저장됨! [오름 ID: $oreumIdx]")
            onSuccess()
        }.addOnFailureListener { e ->
            Log.e("Firestore", "❌ Firestore 스탬프 저장 실패: ${e.message}")
            onFailure(e.message ?: "스탬프 저장에 실패했습니다.")
        }
    }

    private val _favoriteCount = MutableLiveData<Long>()
    val favoriteCount: LiveData<Long> get() = _favoriteCount

    // fetchFavoriteStatus() 함수 내에서 좋아요 개수도 함께 로드
    fun fetchFavoriteStatus(nickname: String, oreumIdx: String) {
        val userDocRef = db.collection("user_info_col").document(nickname)
        val oreumDocRef = db.collection("oreum_info_col").document(oreumIdx)

        userDocRef.get().addOnSuccessListener { userDocument ->
            val favorites = userDocument.get("favorites") as? Map<String, Boolean>
            _isFavorite.value = favorites?.get(oreumIdx) ?: false
        }.addOnFailureListener {
            _isFavorite.value = false
        }

        oreumDocRef.get().addOnSuccessListener { oreumDocument ->
            val count = oreumDocument.getLong("favorite") ?: 0L
            _favoriteCount.value = count
        }.addOnFailureListener {
            _favoriteCount.value = 0L
        }
    }

    // toggleFavorite 성공 시에도 좋아요 개수를 즉시 갱신
    fun toggleFavorite(
        nickname: String,
        oreumIdx: String,
        newIsFavorite: Boolean,
        onSuccess: (Boolean) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userDoc = db.collection("user_info_col").document(nickname)
        val oreumDoc = db.collection("oreum_info_col").document(oreumIdx)

        db.runTransaction { transaction ->
            val userSnap = transaction.get(userDoc)
            val oreumSnap = transaction.get(oreumDoc)

            val favorites = userSnap.get("favorites") as? MutableMap<String, Boolean> ?: mutableMapOf()
            var favoriteCount = oreumSnap.getLong("favorite") ?: 0L

            if (newIsFavorite) {
                favorites[oreumIdx] = true
                favoriteCount++
            } else {
                favorites.remove(oreumIdx)
                if (favoriteCount > 0) favoriteCount--
            }

            transaction.set(userDoc, mapOf("favorites" to favorites), SetOptions.merge())
            transaction.set(oreumDoc, mapOf("favorite" to favoriteCount), SetOptions.merge())

            favoriteCount  // 최종 개수 반환
        }.addOnSuccessListener { finalFavoriteCount ->
            _isFavorite.value = newIsFavorite
            _favoriteCount.value = finalFavoriteCount
            onSuccess(newIsFavorite)
        }.addOnFailureListener {
            onFailure(it.message ?: "Unknown error")
        }
    }

}
