package com.jeong.feature.oreum.presentation.map

import com.jeong.domain.entity.GeoBounds
import com.jeong.domain.entity.GeoPoint
import com.kakao.vectormap.LatLng

internal fun LatLng.asGeoPoint(): GeoPoint = GeoPoint(getLatitude(), getLongitude())

internal fun asGeoBounds(sw: LatLng, ne: LatLng): GeoBounds =
    GeoBounds(
        sw = GeoPoint(sw.getLatitude(), sw.getLongitude()),
        ne = GeoPoint(ne.getLatitude(), ne.getLongitude()),
    )
