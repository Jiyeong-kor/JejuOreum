package com.jeong.jjoreum.presentation.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.FragmentMapBinding
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.MapViewModel
import com.kakao.vectormap.*
import kotlinx.coroutines.launch

/**
 * 지도를 표시하는 Fragment
 * KakaoMapSdk를 사용하여 지도에 오름 위치(마커)를 표시
 */
class MapFragment : ViewBindingBaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate) {

    // 지도 뷰
    private var mapView: MapView? = null

    // MapViewModel
    private lateinit var viewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
    }

    /**
     * View가 생성된 후 호출, 지도 초기화 및 ViewModel의 상태를 관찰
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 지도 뷰 초기화
        mapView = binding?.mapView

        mapView?.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.i("KakaoMap", "Map has been destroyed.")
            }

            override fun onMapError(error: Exception) {
                Log.e("KakaoMap", "Map error occurred: ${error.message}", error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                // Map 준비가 완료되면 ViewModel 초기화 및 UI 업데이트
                viewModel.initializeMap(map)

                // 선택된 오름이 변경되면 BottomSheet를 표시
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.selectedOreum.collect { oreum ->
                            oreum?.let { showOreumDetailBottomSheet(it) }
                        }
                    }
                }

                // POI(마커) 클릭 리스너 설정
                map.setOnPoiClickListener { _, latLng, _, _ ->
                    // 마커 아이콘 변경 및 오름 선택
                    viewModel.changeMarkerIcon(latLng)
                    viewModel.selectOreum(latLng)
                }

                // 오름 리스트가 변경될 때 마커 추가
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.oreumList.collect { oreumList ->
                            oreumList.forEach {
                                viewModel.addOreumMarker(it, R.drawable.oreum_light_pin)
                            }
                        }
                    }
                }
            }
        })
    }

    /**
     * 오름 상세 정보를 표시하는 BottomSheet를 띄우는 함수
     * @param oreum 선택된 오름 정보
     */
    private fun showOreumDetailBottomSheet(oreum: ResultSummary) {
        val bottomSheet = OreumDetailBottomSheetDialogFragment().apply {
            arguments = Bundle().apply {
                // 여기서 idx를 반드시 넣어준다
                putInt("oreumIdx", oreum.idx)
                putString("oreumName", oreum.oreumKname)
                putString("oreumAddr", oreum.oreumAddr)
                putString("oreumExplain", oreum.explain)
                putString("imgPath", oreum.imgPath)
                putDouble("x", oreum.x)
                putDouble("y", oreum.y)
                putDouble("oreumAltitu", oreum.oreumAltitu)
            }
        }
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

}
