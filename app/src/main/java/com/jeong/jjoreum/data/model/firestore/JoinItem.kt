package com.jeong.jjoreum.data.model.firestore

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * 회원 정보를 저장하는 데이터 클래스
 * @param nickname 사용자 닉네임
 * @param id 사용자 ID
 * @param favorites 사용자가 즐겨찾기한 오름 목록
 * @param stampedOreums 사용자가 스탬프를 찍은 오름 목록
 */
@IgnoreExtraProperties
data class JoinItem(
    val nickname: String = "",
    val id: Int = -1,
    val favorites: Map<String, Boolean> = emptyMap(),
    val stampedOreums: Map<String, String> = emptyMap()
) {
    /**
     * Firestore에 저장할 데이터 형태로 변환하는 함수
     * @return Firestore에 저장 가능한 Map 형태의 데이터
     */
    @Exclude
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "nickname" to nickname,
            "favorites" to favorites,
            "stampedOreums" to stampedOreums
        )
    }
}