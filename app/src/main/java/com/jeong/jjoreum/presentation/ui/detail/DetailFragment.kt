package com.jeong.jjoreum.presentation.ui.detail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.local.PreferenceManager
import com.jeong.jjoreum.util.toastMessage
import com.jeong.jjoreum.databinding.FragmentDetailBinding
import com.jeong.jjoreum.data.model.api.RetrofitOkHttpManager
import com.jeong.jjoreum.presentation.ui.base.ViewBindingBaseFragment
import com.jeong.jjoreum.presentation.viewmodel.DetailViewModel
import com.jeong.jjoreum.presentation.viewmodel.OreumListViewModel

/**
 * 상세 화면을 보여주는 Fragment
 * 선택한 오름에 대한 상세 정보를 표시하고, 스탬프 찍기 및 즐겨찾기 등을 처리함
 */
class DetailFragment : ViewBindingBaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {
    // OreumListViewModel를 공유
    private val sharedViewModel: OreumListViewModel by activityViewModels()
    private val detailViewModel: DetailViewModel by viewModels()

    // DetailViewModel을 사용하여 오름 상세 정보와 상태를 관리
    private val viewModel: DetailViewModel by viewModels()
    // Safe Args를 사용하여 전달된 오름 데이터를 받음
    private val args: DetailFragmentArgs by navArgs()

    // 위치 정보를 얻기 위한 FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // 위치 권한 요청 런처
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                toastMessage("위치 권한이 허용되었습니다!")
            } else {
                toastMessage("위치 권한이 필요합니다.")
            }
        }

    /**
     * 위치 권한을 체크하고 요청하는 함수
     */
    private fun checkAndRequestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(permission)
        }
    }

    /**
     * Fragment가 생성된 후 View가 생성될 때 호출
     * UI 초기화와 ViewModel을 연동하고, 클릭 리스너를 설정함
     * @param view 생성된 View
     * @param savedInstanceState 저장된 인스턴스 상태
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 위치 권한 확인 및 요청
        checkAndRequestLocationPermission()

        // 위치 클라이언트 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Safe Args로부터 전달받은 오름 정보
        val oreumData = args.oreumData
        // ViewModel에 오름 정보 설정
        viewModel.setOreumDetail(oreumData)

        // UI 설정 및 ViewModel 관찰
        setupUI()
        observeViewModel()

        val nickname = PreferenceManager.getInstance(requireContext()).getNickname()
        val oreumIdx = oreumData.idx.toString()

        // 스탬프 및 즐겨찾기 상태 조회
        viewModel.fetchStampStatus(nickname, oreumIdx)
        // 좋아요 상태와 개수 즉시 갱신
        viewModel.fetchFavoriteStatus(nickname, oreumIdx)
        // 좋아요 상태 반영
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            binding?.detailFavorite?.isChecked = isFav
        }

        binding?.detailFavorite?.setOnClickListener {
            val newIsFavorite = binding!!.detailFavorite.isChecked
            viewModel.toggleFavorite(
                nickname = nickname,
                oreumIdx = oreumIdx,
                newIsFavorite = newIsFavorite,
                onSuccess = {
                    toastMessage("관심 오름에 추가되었습니다!")
                },
                onFailure = { errMsg ->
                    toastMessage("좋아요 토글 실패: $errMsg")
                }
            )
        }

        // 스탬프 버튼 클릭 시 위치 확인 후 스탬프 처리
        binding?.detailStamp?.setOnClickListener {
            viewModel.checkUserLocation(
                context = requireContext(),
                fusedLocationClient = fusedLocationClient,
                oreumIdx = oreumIdx,
                oreum = oreumData,
                nickname = nickname,
                onSuccess = {
                    toastMessage("스탬프가 인증되었습니다!")
                    // 성공 후 다시 스탬프 상태를 갱신
                    viewModel.fetchStampStatus(nickname, oreumIdx)
                },
                onFailure = { message ->
                    toastMessage(message) // 오류 메시지를 토스트로 표시
                }
            )
        }
    }

    /**
     * UI 구성 요소들을 초기화하는 함수
     * 뒤로가기 버튼 등 기본적인 클릭 리스너를 설정함
     */
    private fun setupUI() {
        binding?.detailToolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * ViewModel을 관찰하여 UI를 업데이트하는 함수
     */
    private fun observeViewModel() {
        viewModel.oreumDetail.observe(viewLifecycleOwner) { oreum ->
            binding?.apply {
                detailName.text = oreum.oreumKname
                detailAddr.text = oreum.oreumAddr
                detailExplain.text = oreum.explain

                val imageLoader = ImageLoader.Builder(requireContext())
                    .okHttpClient { RetrofitOkHttpManager.getUnsafeOkHttpClient() }
                    .build()

                val request = ImageRequest.Builder(requireContext())
                    .data(
                        if (oreum.imgPath.startsWith("http")) oreum.imgPath
                        else "https://example.com/default_image.jpg"
                    )
                    .crossfade(true)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .target(detailImage)
                    .build()

                imageLoader.enqueue(request)

                // 기존의 즐겨찾기 상태 관찰 유지
                viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
                    detailFavorite.isChecked = isFavorite
                }

                // 스탬프 획득 여부 관찰 유지
                viewModel.hasStamp.observe(viewLifecycleOwner) { hasStamp ->
                    detailStamp.text = if (hasStamp) "후기 작성" else "스탬프 찍기"
                }
            }
        }
    }

}
