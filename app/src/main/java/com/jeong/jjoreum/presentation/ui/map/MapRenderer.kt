package com.jeong.jjoreum.presentation.ui.map

import android.util.Log
import com.jeong.jjoreum.R
import com.jeong.jjoreum.domain.geo.GeoPoint
import com.jeong.jjoreum.domain.geo.quantized
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.CompetitionType
import com.kakao.vectormap.label.CompetitionUnit
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelLayerOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle
import com.kakao.vectormap.label.OrderingType

class MapRenderer(private val map: KakaoMap) {

    private val labelLayer: LabelLayer = map.labelManager?.addLayer(
        LabelLayerOptions.from("oreumLayer")
            .setOrderingType(OrderingType.Rank)
            .setCompetitionUnit(CompetitionUnit.IconAndText)
            .setCompetitionType(CompetitionType.All),
    )?.apply {
        isVisible = true
        isClickable = true
    } ?: error("LabelLayer 생성 실패")

    private val markersByPoint = mutableMapOf<GeoPoint, Label>()
    private var selectedLabel: Label? = null

    private val unselectedStyle: LabelStyles by lazy(LazyThreadSafetyMode.NONE) {
        LabelStyles.from(
            LabelStyle.from(R.drawable.oreum_unselected).apply {
                setTextStyles(
                    LabelTextStyle
                        .from(24, 0xFF000000.toInt(), 0, 0)
                )
            },
        )
    }
    private val selectedStyle: LabelStyles by lazy(LazyThreadSafetyMode.NONE) {
        LabelStyles.from(
            LabelStyle.from(R.drawable.oreum_selected).apply {
                setTextStyles(
                    LabelTextStyle
                        .from(32, 0xFF000000.toInt(), 0, 0)
                )
            },
        )
    }

    private fun MapPinUi.quantKey(): GeoPoint = GeoPoint(lat, lon).quantized()
    private fun LatLng.quantKey(): GeoPoint = GeoPoint(latitude, longitude).quantized()

    fun syncMarkers(pins: List<MapPinUi>) {
        val newKeyByPin = pins.associateBy { it.quantKey() }
        val oldKeys = markersByPoint.keys
        val newKeys = newKeyByPin.keys

        if (oldKeys.size == newKeys.size && oldKeys.containsAll(newKeys)) return

        val wasVisible = labelLayer.isVisible
        if (wasVisible) labelLayer.isVisible = false
        try {
            (oldKeys - newKeys).forEach { key ->
                markersByPoint.remove(key)?.let { labelLayer.remove(it) }
            }
            (newKeys - oldKeys).forEach { key ->
                val p = newKeyByPin.getValue(key)
                val label = labelLayer.addLabel(
                    com.kakao.vectormap.label.LabelOptions
                        .from(LatLng.from(p.lat, p.lon))
                        .setTexts(LabelTextBuilder().setTexts(p.title))
                        .setStyles(unselectedStyle)
                        .setTag("oreum:${p.title}")
                )
                markersByPoint[key] = label
            }
        } finally {
            labelLayer.isVisible = wasVisible
        }
    }

    fun selectMarkerAt(latLng: LatLng) {
        selectedLabel?.changeStyles(unselectedStyle)
        val label = markersByPoint[latLng.quantKey()]
        label?.changeStyles(selectedStyle)
        selectedLabel = label
    }

    fun clearSelection() {
        selectedLabel?.changeStyles(unselectedStyle)
        selectedLabel = null
    }

    fun moveCameraTo(latLng: LatLng) {
        map.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
    }

    fun release() {
        runCatching { labelLayer.removeAll() }
            .onFailure { Log.w("MapRenderer", "removeAll 실패", it) }
        markersByPoint.clear()
        selectedLabel = null
    }
}
