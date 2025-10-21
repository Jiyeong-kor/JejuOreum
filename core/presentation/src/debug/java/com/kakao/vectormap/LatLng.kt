package com.kakao.vectormap

import kotlin.jvm.JvmName

class LatLng private constructor(
    private val latitudeValue: Double,
    private val longitudeValue: Double,
) {
    fun getLatitude(): Double = latitudeValue
    fun getLongitude(): Double = longitudeValue

    @get:JvmName("latitudeValue")
    val latitude: Double
        get() = latitudeValue

    @get:JvmName("longitudeValue")
    val longitude: Double
        get() = longitudeValue

    companion object {
        fun from(latitude: Double, longitude: Double): LatLng = LatLng(latitude, longitude)
    }
}
