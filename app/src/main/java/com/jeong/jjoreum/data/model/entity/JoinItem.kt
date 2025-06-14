package com.jeong.jjoreum.data.model.entity

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
    val uid: String = "",
    val nickname: String = "",
    val favorites: Map<String, Boolean> = emptyMap(),
    val stampedOreums: Map<String, String> = emptyMap()
) {
    @Exclude
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "nickname" to nickname,
            "favorites" to favorites,
            "stampedOreums" to stampedOreums
        )
    }
}