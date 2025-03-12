package com.jeong.jjoreum.presentation.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.databinding.FragmentOreumDetailBottomSheetBinding

/**
 * 지도에서 오름을 클릭했을 때, 하단에 오름 상세 정보를 표시하는 BottomSheetDialogFragment
 */
class OreumDetailBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentOreumDetailBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOreumDetailBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * View가 생성된 뒤, 전달받은 인자(오름 정보)를 통해 UI를 설정
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 번들에서 오름 정보 추출
        val oreumIdx = arguments?.getInt("oreumIdx") ?: 0  // mapIndexed에서 부여된 값
        val oreumKname = arguments?.getString("oreumName") ?: "오름"
        val oreumAddr = arguments?.getString("oreumAddr") ?: "주소 정보 없음"
        val oreumExplain = arguments?.getString("oreumExplain") ?: "설명 없음"
        val imgPath = arguments?.getString("imgPath") ?: ""
        val x = arguments?.getDouble("x") ?: 0.0
        val y = arguments?.getDouble("y") ?: 0.0
        val oreumAltitu = arguments?.getDouble("oreumAltitu") ?: 0.0

        // UI에 데이터 설정
        binding.bottomDetailName.text = oreumKname
        binding.bottomDetailAddr.text = oreumAddr

        // Firestore에서 해당 오름의 favorite, stamp 카운트 불러오기
        val oreumDocRef = Firebase.firestore.collection("oreum_info_col").document(oreumIdx.toString())
        oreumDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoriteCount = document.getLong("favorite") ?: 0
                    val stampCount = document.getLong("stamp") ?: 0

                    binding.bottomDetailFavoriteCount.text = "$favoriteCount"
                    binding.bottomDetailStampCount.text = "$stampCount"
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Firestore 데이터 로드 실패: ${e.message}")
            }

        // BottomSheet 클릭 시 상세 화면으로 이동
        binding.root.setOnClickListener {
            val resultSummary = ResultSummary(
                idx = oreumIdx,
                oreumEname = "",
                oreumKname = oreumKname,
                oreumAddr = oreumAddr,
                oreumAltitu = oreumAltitu,
                x = x,
                y = y,
                explain = oreumExplain,
                imgPath = imgPath
            )

            try {
                // BottomSheet 닫기
                dismiss()

                requireActivity().runOnUiThread {
                    val navController = requireActivity().findNavController(R.id.container_main)

                    val action =
                        MapFragmentDirections.actionMapFragmentToDetailFragment(resultSummary)
                    navController.navigate(action)
                }

            } catch (e: IllegalStateException) {
                Log.e("OreumDetailBottomSheet", "🚨 네비게이션 실행 중 오류 발생: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
