package com.jeong.jjoreum.data.model.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

/**
 * 오름 리스트 API 응답을 처리하는 데이터 클래스
 * @param resultCode 응답 코드
 * @param resultMsg 응답 메시지
 * @param resultSummary 오름 정보 리스트
 */
data class OreumData(
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String,
    val resultSummary: List<ResultSummary>
)

@Parcelize
data class ResultSummary(
    val idx: Int = -1,
    @SerializedName("oleumEname") val oreumEname: String = "",
    @SerializedName("oleumKname") val oreumKname: String = "",
    @SerializedName("oleumAddr") val oreumAddr: String = "",
    @SerializedName("oleumAltitu") val oreumAltitu: Double = 0.0,
    val x: Double = 0.0,
    val y: Double = 0.0,
    @SerializedName("explan") val explain: String = "",
    @SerializedName("imgPath") val imgPath: String = "",

    // 전체 좋아요 및 스탬프 수
    @SerializedName("favorite") val totalFavorites: Int = 0,
    @SerializedName("stamp") val totalStamps: Int = 0,

    // 사용자의 상태 (Firestore에서 매핑해서 따로 설정할 값)
    var userLiked: Boolean = false,
    var userStamped: Boolean = false

) : Parcelable