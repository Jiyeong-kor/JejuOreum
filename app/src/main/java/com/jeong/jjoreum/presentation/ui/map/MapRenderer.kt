package com.jeong.jjoreum.presentation.ui.map

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
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextBuilder
import com.kakao.vectormap.label.LabelTextStyle
import com.kakao.vectormap.label.OrderingType
import timber.log.Timber

class MapRenderer(private val map: KakaoMap) {
    private val labelLayer: LabelLayer? = runCatching {
        map.labelManager?.addLayer(
            LabelLayerOptions.from("oreumLayer")
                .setOrderingType(OrderingType.Rank)
                .setCompetitionUnit(CompetitionUnit.IconAndText)
                .setCompetitionType(CompetitionType.All)
        )?.apply {
            isVisible = true
            isClickable = true
        }
    }.getOrElse {
        Timber.e(it, "LabelLayer 생성 실패")
        null
    }

    private data class MarkerEntry(val pin: MapPinUi, val label: Label)

    private val markersByPoint = mutableMapOf<GeoPoint, MarkerEntry>()
    private var selectedLabel: Label? = null
    private val unselectedStyle: LabelStyles by lazy(LazyThreadSafetyMode.NONE) {
        LabelStyles.from(
            LabelStyle.from(R.drawable.oreum_unselected).apply {
                setTextStyles(
                    LabelTextStyle
                        .from(24, 0xFF000000.toInt(), 0, 0)
                )
            }
        )
    }
    private val selectedStyle: LabelStyles by lazy(LazyThreadSafetyMode.NONE) {
        LabelStyles.from(
            LabelStyle.from(R.drawable.oreum_selected).apply {
                setTextStyles(
                    LabelTextStyle
                        .from(32, 0xFF000000.toInt(), 0, 0)
                )
            }
        )
    }
    private val clusterStyle: LabelStyles by lazy(LazyThreadSafetyMode.NONE) {
        LabelStyles.from(
            LabelStyle.from(R.drawable.oreum_selected).apply {
                setTextStyles(
                    LabelTextStyle
                        .from(28, 0xFFFFFFFF.toInt(), 0, 0)
                )
            }
        )
    }

    private fun MapPinUi.quantKey(): GeoPoint = GeoPoint(lat, lon).quantized()
    private fun LatLng.quantKey(): GeoPoint = GeoPoint(latitude, longitude).quantized()

    fun syncMarkers(pins: List<MapPinUi>) {
        val layer = labelLayer ?: return
        val newKeyByPin = pins.associateBy { it.quantKey() }
        val oldKeys = markersByPoint.keys.toSet()
        val newKeys = newKeyByPin.keys
        if (oldKeys.size == newKeys.size && oldKeys.containsAll(newKeys)) {
            val allSame = oldKeys.all { key ->
                markersByPoint[key]?.pin == newKeyByPin[key]
            }
            if (allSame) return
        }
        val wasVisible = layer.isVisible
        if (wasVisible) layer.isVisible = false
        try {
            (oldKeys - newKeys).forEach { key ->
                markersByPoint.remove(key)?.let { entry ->
                    if (selectedLabel == entry.label) {
                        entry.label.changeStyles(unselectedStyle)
                        selectedLabel = null
                    }
                    layer.remove(entry.label)
                }
            }
            (newKeys - oldKeys).forEach { key ->
                val pin = newKeyByPin.getValue(key)
                val label = layer.addLabel(pin.createLabelOptions())
                markersByPoint[key] = MarkerEntry(pin, label)
            }
            (oldKeys intersect newKeys).forEach { key ->
                val newPin = newKeyByPin.getValue(key)
                val oldEntry = markersByPoint.getValue(key)
                if (oldEntry.pin != newPin) {
                    if (selectedLabel == oldEntry.label) {
                        oldEntry.label.changeStyles(unselectedStyle)
                        selectedLabel = null
                    }
                    layer.remove(oldEntry.label)
                    val label = layer.addLabel(newPin.createLabelOptions())
                    markersByPoint[key] = MarkerEntry(newPin, label)
                }
            }
        } finally {
            layer.isVisible = wasVisible
        }
    }

    fun selectMarkerAt(latLng: LatLng): MapPinUi? {
        val entry = markersByPoint[latLng.quantKey()] ?: return null
        val pin = entry.pin
        if (pin is MapPinUi.Single) {
            selectedLabel?.changeStyles(unselectedStyle)
            entry.label.changeStyles(selectedStyle)
            selectedLabel = entry.label
        } else {
            selectedLabel?.changeStyles(unselectedStyle)
            selectedLabel = null
        }
        return pin
    }

    fun clearSelection() {
        selectedLabel?.changeStyles(unselectedStyle)
        selectedLabel = null
    }

    fun moveCameraTo(latLng: LatLng) {
        map.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
    }

    fun release() {
        runCatching { labelLayer?.removeAll() }
            .onFailure { Timber.w(it, "removeAll 실패") }
        markersByPoint.clear()
        selectedLabel = null
    }

    private fun MapPinUi.createLabelOptions(): LabelOptions = when (this) {
        is MapPinUi.Cluster -> LabelOptions
            .from(LatLng.from(lat, lon))
            .setTexts(LabelTextBuilder().setTexts(count.toString()))
            .setStyles(clusterStyle)
            .setTag("oreum:cluster:$count")

        is MapPinUi.Single -> LabelOptions
            .from(LatLng.from(lat, lon))
            .setTexts(LabelTextBuilder().setTexts(title))
            .setStyles(unselectedStyle)
            .setTag("oreum:$oreumId")
    }
}
