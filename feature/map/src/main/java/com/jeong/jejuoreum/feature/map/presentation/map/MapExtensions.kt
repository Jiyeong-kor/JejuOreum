package com.jeong.jejuoreum.feature.map.presentation.map

import com.jeong.jejuoreum.domain.oreum.entity.GeoBounds
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.kakao.vectormap.LatLng

internal fun LatLng.asGeoPoint(): GeoPoint = GeoPoint(getLatitude(), getLongitude())

internal fun asGeoBounds(sw: LatLng, ne: LatLng): GeoBounds =
    GeoBounds(
        sw = GeoPoint(sw.getLatitude(), sw.getLongitude()),
        ne = GeoPoint(ne.getLatitude(), ne.getLongitude()),
    )
