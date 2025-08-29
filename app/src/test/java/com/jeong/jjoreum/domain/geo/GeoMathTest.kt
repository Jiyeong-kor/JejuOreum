package com.jeong.jjoreum.domain.geo

import org.junit.Assert.assertEquals
import org.junit.Test

class GeoMathTest {
    @Test
    fun quantizeRemovesNegativeZero() {
        // 음수 영(-0.0) 좌표가 정규화되어 해시 충돌이 발생하지 않는지 확인
        val point = GeoPoint(-1e-8, 1e-8).quantized()
        val origin = GeoPoint(0.0, 0.0)
        assertEquals(origin, point)
        assertEquals(origin.hashCode(), point.hashCode())
    }
}
