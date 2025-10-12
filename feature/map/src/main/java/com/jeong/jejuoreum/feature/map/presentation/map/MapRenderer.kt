package com.jeong.jejuoreum.feature.map.presentation.map

import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import com.jeong.jejuoreum.domain.oreum.entity.GeoPoint
import com.jeong.jejuoreum.domain.oreum.entity.quantized
import com.jeong.jejuoreum.feature.map.R
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

class MapRenderer(
    private val map: KakaoMap,
    private val resources: Resources,
) {
    private val layerId: String = resources.getString(R.string.map_label_layer_id)
    private val markerTagPattern: String = resources.getString(R.string.map_marker_tag_pattern)
    private val unselectedTextSizePx: Int =
        resources.getDimensionPixelSize(R.dimen.map_marker_text_size_unselected)
    private val selectedTextSizePx: Int =
        resources.getDimensionPixelSize(R.dimen.map_marker_text_size_selected)
    @ColorInt
    private val markerTextColor: Int = ResourcesCompat.getColor(
        resources,
        R.color.map_marker_label_text,
        null,
    )

    private val labelLayer: LabelLayer? = runCatching {
        map.labelManager?.addLayer(
            LabelLayerOptions.from(layerId)
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
    private val markersByPoint = mutableMapOf<GeoPoint, Label>()
    private var selectedLabel: Label? = null
    private val unselectedStyle: LabelStyles by lazy(LazyThreadSafetyMode.NONE) {
        LabelStyles.from(
            LabelStyle.from(R.drawable.oreum_marker_unselected).apply {
                setTextStyles(
                    LabelTextStyle.from(
                        unselectedTextSizePx,
                        markerTextColor,
                        0,
                        0,
                    ),
                )
            },
        )
    }
    private val selectedStyle: LabelStyles by lazy(LazyThreadSafetyMode.NONE) {
        LabelStyles.from(
            LabelStyle.from(R.drawable.oreum_marker_selected).apply {
                setTextStyles(
                    LabelTextStyle.from(
                        selectedTextSizePx,
                        markerTextColor,
                        0,
                        0,
                    ),
                )
            },
        )
    }

    private fun MapPinUiModel.quantKey(): GeoPoint = GeoPoint(lat, lon).quantized()
    private fun LatLng.quantKey(): GeoPoint = GeoPoint(latitude, longitude).quantized()

    fun syncMarkers(pins: List<MapPinUiModel>) {
        val layer = labelLayer ?: return
        val newKeyByPin = pins.associateBy { it.quantKey() }
        val oldKeys = markersByPoint.keys
        val newKeys = newKeyByPin.keys
        if (oldKeys.size == newKeys.size && oldKeys.containsAll(newKeys)) return

        val wasVisible = layer.isVisible
        if (wasVisible) layer.isVisible = false
        try {
            (oldKeys - newKeys).forEach { key ->
                markersByPoint.remove(key)?.let { layer.remove(it) }
            }
            (newKeys - oldKeys).forEach { key ->
                val p = newKeyByPin.getValue(key)
                val label = layer.addLabel(
                    LabelOptions
                        .from(LatLng.from(p.lat, p.lon))
                        .setTexts(LabelTextBuilder().setTexts(p.title))
                        .setStyles(unselectedStyle)
                        .setTag(markerTagFor(p.title))
                )
                markersByPoint[key] = label
            }
        } finally {
            layer.isVisible = wasVisible
        }
    }

    private fun markerTagFor(title: String): String = markerTagPattern.format(title)

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
        runCatching { labelLayer?.removeAll() }
            .onFailure { Timber.w(it, "removeAll 실패") }
        markersByPoint.clear()
        selectedLabel = null
    }
}
