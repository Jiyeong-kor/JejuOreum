package com.jeong.jjoreum.domain.geo

import kotlin.math.pow
import kotlin.math.round

/** 기본 양자화 소수 자리(≈ 5자리 → 적도 기준 약 1.1m) */
const val GEO_QUANT_DECIMALS: Int = 5

/**
 * 실수를 소수 [decimals] 자리까지 반올림(좌표 스냅용)
 */
fun Double.quantize(decimals: Int = GEO_QUANT_DECIMALS): Double {
    val s = 10.0.pow(decimals)
    val q = round(this * s) / s
    // Kotlin/Java는 -0.0에 대해 부호 비트를 유지하여
    // (0.0 == -0.0)임에도 해시코드가 달라지는 문제가 발생합니다.
    // GeoPoint를 해시 기반 컬렉션에서 사용할 때 예기치 않은 동작을 막기 위해
    // 값을 양수 0.0으로 정규화합니다.
    return if (q == -0.0) 0.0 else q
}

/**
 * 좌표를 소수 [decimals] 자리까지 스냅(동일 지점 판단 등에서 사용)
 */
fun GeoPoint.quantized(decimals: Int = GEO_QUANT_DECIMALS): GeoPoint =
    GeoPoint(lat.quantize(decimals), lon.quantize(decimals))
