package com.jeong.jjoreum.presentation.ui.map

import com.jeong.jjoreum.domain.geo.GeoBounds
import com.jeong.jjoreum.domain.geo.GeoPoint
import com.kakao.vectormap.LatLng

internal fun LatLng.asGeoPoint(): GeoPoint = GeoPoint(getLatitude(), getLongitude())

internal fun asGeoBounds(sw: LatLng, ne: LatLng): GeoBounds =
    GeoBounds(
        sw = GeoPoint(sw.getLatitude(), sw.getLongitude()),
        ne = GeoPoint(ne.getLatitude(), ne.getLongitude()),
    )
