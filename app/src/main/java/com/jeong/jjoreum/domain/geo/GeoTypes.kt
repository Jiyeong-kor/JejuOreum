package com.jeong.jjoreum.domain.geo

/**
 * 도메인 레벨의 위경도 좌표(지도 SDK에 의존하지 않음)
 */
data class GeoPoint(val lat: Double, val lon: Double)

/**
 * 남서(SW)–북동(NE) 꼭짓점으로 표현한 뷰포트 경계
 */
data class GeoBounds(val sw: GeoPoint, val ne: GeoPoint)
