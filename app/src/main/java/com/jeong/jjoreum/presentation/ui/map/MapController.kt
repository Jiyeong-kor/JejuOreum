package com.jeong.jjoreum.presentation.ui.map

import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.LatLngBounds
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

/**
 * 지도 조작과 관련된 UI
 * - 마커 추가, 강조, 제거
 * - 카메라 이동
 * - 지도 이벤트 리스너 등록
 *
 * @property map
 * @property onPoiClicked
 * @constructor Create empty Map controller
 */
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
        // 지도 클릭 이벤트 리스너 등록
        map.setOnPoiClickListener { _, latLng, _, _ -> onPoiClicked(latLng) }
        map.setOnViewportClickListener { _, _, _ -> /* 빈 영역 클릭은 외부에서 처리 */ }

        // 제주도로 카메라 이동
        map.moveCamera(
            CameraUpdateFactory.fitMapPoints(
                LatLngBounds(
                    LatLng.from(33.1, 126.1),
                    LatLng.from(33.65, 126.95)
                ),
                100
            )
        )
    }

    /**
     * 오름 리스트를 지도에 마커로 표시
     *
     * @param list
     * @param iconResId
     */
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

    /**
     * 특정 마커를 강조 (이전 강조는 원상 복구)
     *
     * @param latLng
     */
    fun highlightMarker(latLng: LatLng) {
        previousSelectedLabel?.changeStyles(createLabelStyles(R.drawable.oreum_unselected, 24))
        val label = oreumMarkers.entries.find { isSameLocation(it.key, latLng) }?.value
        label?.changeStyles(createLabelStyles(R.drawable.oreum_selected, 32))
        previousSelectedLabel = label
    }

    /**
     * 카메라를 특정 위치로 이동
     *
     * @param latLng
     */
    fun moveCameraTo(latLng: LatLng) {
        map.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
    }

    /**
     * 모든 마커 제거
     *
     */
    private fun clearAllMarkers() {
        labelLayer.removeAll()
        oreumMarkers.clear()
        previousSelectedLabel = null
    }

    /**
     * 마커 스타일 생성 (아이콘 + 텍스트)
     *
     * @param iconResId
     * @param textSize
     * @return
     */
    private fun createLabelStyles(iconResId: Int, textSize: Int): LabelStyles = LabelStyles.from(
        LabelStyle.from(iconResId).apply {
            setTextStyles(
                LabelTextStyle.from(
                    textSize, 0xFF000000.toInt(), 0, 0
                )
            )
        }
    )

    /**
     * 좌표가 거의 같은지 판별 (마커 중복 방지 및 선택 강조)
     *
     * @param a
     * @param b
     * @return
     */
    private fun isSameLocation(a: LatLng, b: LatLng): Boolean =
        kotlin.math.abs(a.latitude - b.latitude) < 0.0001 &&
                kotlin.math.abs(a.longitude - b.longitude) < 0.0001

}