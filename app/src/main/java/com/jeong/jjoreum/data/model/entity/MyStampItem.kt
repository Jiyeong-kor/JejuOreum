package com.jeong.jjoreum.data.model.entity

/**
 * 사용자가 획득한 스탬프 정보를 저장하는 데이터 클래스
 * @param userId 사용자 ID
 * @param oreumIdx 오름의 인덱스 (고유 식별자)
 * @param oreumName 오름 이름
 * @param stampBoolean 스탬프 획득 여부 (true: 획득함, false: 미획득)
 */
data class MyStampItem(
    val userId: Int = -1,
    val oreumIdx: Int = -1,
    val oreumName: String = "",
    val stampBoolean: Boolean = false
)
