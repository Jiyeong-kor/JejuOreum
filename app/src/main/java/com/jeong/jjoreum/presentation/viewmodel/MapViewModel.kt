package com.jeong.jjoreum.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.repository.MapRepository
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * MapFragment에서 사용되는 ViewModel
 * 카카오 지도에서 오름 마커를 생성/관리하고,
 * 사용자가 선택한 오름 데이터를 StateFlow로 관리함
 * @param application 애플리케이션 컨텍스트
 * @param repository 지도 관련 데이터(오름 리스트 등)를 제공하는 Repository
 */
class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MapRepository(application)

    // 오름 리스트
    private val _oreumList = MutableStateFlow<List<ResultSummary>>(emptyList())
    val oreumList: StateFlow<List<ResultSummary>> = _oreumList.asStateFlow()

    // 카카오 맵 관련 객체
    private var kakaoMap: KakaoMap? = null
    private var labelLayer: LabelLayer? = null
    private var labelManager: LabelManager? = null
    private val oreumMarkers = mutableMapOf<LatLng, Label>()

    // 선택된 오름
    private val _selectedOreum = MutableStateFlow<ResultSummary?>(null)
    val selectedOreum: StateFlow<ResultSummary?> = _selectedOreum.asStateFlow()

    // 이전에 선택된 마커 Label
    private var previousSelectedLabel: Label? = null

    init {
        // ViewModel 초기화 시 오름 리스트 로드
        fetchOreumList()
    }

    /**
     * 서버에서 오름 리스트를 가져와서 StateFlow에 저장
     */
    private fun fetchOreumList() {
        viewModelScope.launch {
            try {
                val oreumList = repository.getOreumList()
                _oreumList.value = oreumList
                Log.d("OreumListViewModel", "✅ 오름 데이터 로드 성공: ${oreumList.size}개")
            } catch (e: Exception) {
                Log.e("OreumListViewModel", "❌ 데이터 로드 실패: ${e.message}")
            }
        }
    }

    /**
     * 지도 초기화 및 기본 위치(제주 범위) 설정
     * @param map KakaoMap 객체
     */
    fun initializeMap(map: KakaoMap) {
        kakaoMap = map
        labelManager = map.labelManager
        initializeLabelLayer()

        val bounds = LatLngBounds(
            LatLng.from(33.1, 126.1), // 남서쪽
            LatLng.from(33.65, 126.95) // 북동쪽
        )
        kakaoMap?.moveCamera(CameraUpdateFactory.fitMapPoints(bounds, 100))
    }

    /**
     * 라벨 레이어를 초기화
     */
    private fun initializeLabelLayer() {
        if (labelLayer == null) {
            labelLayer = labelManager?.addLayer(
                LabelLayerOptions.from("userLocationLayer")
                    .setOrderingType(OrderingType.Rank)
                    .setCompetitionUnit(CompetitionUnit.IconAndText)
                    .setCompetitionType(CompetitionType.All)
            )
            labelLayer?.isVisible = true
            labelLayer?.isClickable = true
        }
    }

    /**
     * 지도에 오름 마커를 추가
     * @param oreum 오름 정보
     * @param iconResId 사용할 마커 아이콘 리소스 ID
     */
    fun addOreumMarker(oreum: ResultSummary, iconResId: Int) {
        labelLayer?.let { layer ->
            val latLng = LatLng.from(oreum.y, oreum.x)
            val label = layer.addLabel(
                LabelOptions.from(latLng)
                    .setTexts(LabelTextBuilder().setTexts(oreum.oreumKname))
                    .setStyles(LabelStyles.from(LabelStyle.from(iconResId)))
                    .setTag(oreum.oreumKname)
            )
            label?.let { oreumMarkers[latLng] = it }
        }
    }

    /**
     * 선택한 오름의 마커 아이콘을 변경
     * @param latLng 사용자가 클릭한 위치
     */
    fun changeMarkerIcon(latLng: LatLng) {
        val label = oreumMarkers.entries.find { (storedLatLng, _) ->
            abs(storedLatLng.latitude - latLng.latitude) < 0.0001 &&
                    abs(storedLatLng.longitude - latLng.longitude) < 0.0001
        }?.value

        // 이전에 선택된 마커가 있다면 원래 아이콘으로 복원
        previousSelectedLabel?.changeStyles(
            LabelStyles.from(LabelStyle.from(R.drawable.oreum_light_pin))
        )

        // 새로 선택된 마커 아이콘 변경
        label?.changeStyles(
            LabelStyles.from(LabelStyle.from(R.drawable.oreum_dark_pin))
        )

        previousSelectedLabel = label
    }

    /**
     * 사용자가 클릭한 위치에 해당하는 오름을 선택하여 상태로 저장
     * @param latLng 사용자가 클릭한 위치
     */
    fun selectOreum(latLng: LatLng) {
        val oreum = _oreumList.value.find {
            abs(it.y - latLng.latitude) < 0.0001 && abs(it.x - latLng.longitude) < 0.0001
        }
        _selectedOreum.value = oreum
    }
}
