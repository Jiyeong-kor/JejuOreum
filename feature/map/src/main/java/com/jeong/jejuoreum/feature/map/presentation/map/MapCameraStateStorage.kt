package com.jeong.jejuoreum.feature.map.presentation.map

import androidx.lifecycle.SavedStateHandle
import com.jeong.jejuoreum.core.navigation.OreumNavigation
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import javax.inject.Inject

internal class MapCameraStateStorage @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) {

    fun persist(snapshot: CameraSnapshot) {
        savedStateHandle[OreumNavigation.Map.SavedStateKeys.CAMERA_LATITUDE] = snapshot.center.lat
        savedStateHandle[OreumNavigation.Map.SavedStateKeys.CAMERA_LONGITUDE] = snapshot.center.lon
        savedStateHandle[OreumNavigation.Map.SavedStateKeys.CAMERA_ZOOM] = snapshot.zoomLevel
    }

    fun restore(): CameraSnapshot? {
        val lat = savedStateHandle.get<Double>(OreumNavigation.Map.SavedStateKeys.CAMERA_LATITUDE)
        val lon = savedStateHandle.get<Double>(OreumNavigation.Map.SavedStateKeys.CAMERA_LONGITUDE)
        val zoom = savedStateHandle.get<Int>(OreumNavigation.Map.SavedStateKeys.CAMERA_ZOOM)

        return if (lat != null && lon != null && zoom != null) {
            CameraSnapshot(GeoPoint(lat, lon), zoom)
        } else {
            null
        }
    }
}
