package com.kakao.vectormap.camera

import com.kakao.vectormap.LatLng

class CameraUpdate internal constructor(
    val center: LatLng,
    val zoomLevel: Int?,
)

object CameraUpdateFactory {
    fun newCenterPosition(latLng: LatLng): CameraUpdate = CameraUpdate(latLng, null)

    fun newCenterPosition(latLng: LatLng, zoomLevel: Int): CameraUpdate = CameraUpdate(latLng, zoomLevel)
}
