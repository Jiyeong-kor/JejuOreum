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
    return round(this * s) / s
}

/**
 * 좌표를 소수 [decimals] 자리까지 스냅(동일 지점 판단 등에서 사용)
 */
fun GeoPoint.quantized(decimals: Int = GEO_QUANT_DECIMALS): GeoPoint =
    GeoPoint(lat.quantize(decimals), lon.quantize(decimals))
