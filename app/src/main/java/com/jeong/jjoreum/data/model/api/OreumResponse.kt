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

/**
 * 오름의 상세 정보를 저장하는 데이터 클래스
 * @param idx 오름의 고유 식별자
 * @param oreumEname 오름의 영문 이름
 * @param oreumKname 오름의 한글 이름
 * @param oreumAddr 오름의 주소
 * @param oreumAltitu 오름의 고도
 * @param x 오름의 경도 (Longitude)
 * @param y 오름의 위도 (Latitude)
 * @param explain 오름 설명
 * @param imgPath 오름 이미지 경로
 * @param favorite 즐겨찾기 여부 (1 이상: 즐겨찾기한 오름, 0: 즐겨찾기 안 함)
 * @param stamp 스탬프 획득 여부 (1 이상: 스탬프 찍음, 0: 스탬프 없음)
 */
@Parcelize
data class ResultSummary(
    val idx: Int,
    @SerializedName("oleumEname")
    val oreumEname: String,
    @SerializedName("oleumKname")
    val oreumKname: String,
    @SerializedName("oleumAddr")
    val oreumAddr: String,
    @SerializedName("oleumAltitu")
    val oreumAltitu: Double,
    val x: Double,
    val y: Double,
    @SerializedName("explan")
    val explain: String,
    @SerializedName("imgPath")
    val imgPath: String,
    val favorite: Int = 0,
    val stamp: Int = 0
) : Parcelable {
    val isFavorite: Boolean
        get() = favorite > 0

    val hasStamp: Boolean
        get() = stamp > 0
}
