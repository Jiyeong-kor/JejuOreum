package com.jeong.jjoreum.presentation.ui.map

import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
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

class MapController(
    private val map: KakaoMap,
    private val onPoiClicked: (LatLng) -> Unit,
) {
    private val labelLayer: LabelLayer = map.labelManager?.addLayer(
        LabelLayerOptions.from("oreumLayer")
            .setOrderingType(OrderingType.Rank)
            .setCompetitionUnit(CompetitionUnit.IconAndText)
            .setCompetitionType(CompetitionType.All)
    )?.apply {
        isVisible = true
        isClickable = true
    } ?: throw IllegalStateException("LabelLayer 생성 실패")

    private val oreumMarkers = mutableMapOf<LatLng, Label>()
    private var previousSelectedLabel: Label? = null

    init {
        map.setOnPoiClickListener { _, latLng, _, _ -> onPoiClicked(latLng) }
        map.setOnViewportClickListener { _, _, _ -> /* 빈 영역 클릭은 외부에서 처리 */ }
    }

    fun drawOreumMarkers(list: List<ResultSummary>, iconResId: Int = R.drawable.oreum_unselected) {
        clearAllMarkers()
        list.forEach {
            val latLng = LatLng.from(it.y, it.x)
            val label = labelLayer.addLabel(
                LabelOptions.from(latLng)
                    .setTexts(LabelTextBuilder().setTexts(it.oreumKname))
                    .setStyles(createLabelStyles(iconResId, 24))
                    .setTag("oreum:${it.oreumKname}")
            )
            oreumMarkers[latLng] = label
        }
    }

    fun highlightMarker(latLng: LatLng) {
        previousSelectedLabel?.changeStyles(
            createLabelStyles(R.drawable.oreum_unselected, 24)
        )
        val label = oreumMarkers.entries.find { isSameLocation(it.key, latLng) }?.value
        label?.changeStyles(
            createLabelStyles(R.drawable.oreum_selected, 32)
        )
        previousSelectedLabel = label
    }

    fun moveCameraTo(latLng: LatLng) {
        map.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
    }

    private fun clearAllMarkers() {
        labelLayer.removeAll()
        oreumMarkers.clear()
        previousSelectedLabel = null
    }

    private fun createLabelStyles(iconResId: Int, textSize: Int): LabelStyles = LabelStyles.from(
        LabelStyle.from(iconResId).apply {
            setTextStyles(
                LabelTextStyle.from(
                    textSize, 0xFF000000.toInt(), 0, 0
                )
            )
        }
    )

    private fun isSameLocation(a: LatLng, b: LatLng): Boolean =
        kotlin.math.abs(a.latitude - b.latitude) < 0.0001 &&
                kotlin.math.abs(a.longitude - b.longitude) < 0.0001

}