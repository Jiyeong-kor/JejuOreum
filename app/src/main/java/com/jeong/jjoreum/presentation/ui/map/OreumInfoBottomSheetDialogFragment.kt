package com.jeong.jjoreum.presentation.ui.map

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jeong.jjoreum.data.model.api.ResultSummary
import com.jeong.jjoreum.databinding.FragmentOreumInfoBottomSheetBinding
import com.jeong.jjoreum.R

/**
 * 오름 상세 정보 보여주는 BottomSheet
 *
 * @constructor Create empty Oreum info bottom sheet dialog fragment
 */
class OreumInfoBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentOreumInfoBottomSheetBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_OREUM = "oreum"

        /**
         * 오름 정보를 포함한 인스턴스 생성
         *
         * @param oreum
         * @return
         */
        fun newInstance(oreum: ResultSummary): OreumInfoBottomSheetDialogFragment {
            val fragment = OreumInfoBottomSheetDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_OREUM, oreum)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOreumInfoBottomSheetBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Parcelable 객체로부터 오름 정보 읽기 (SDK 버전에 따라 분기)
        val oreum: ResultSummary? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_OREUM, ResultSummary::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_OREUM)
        }


        with(binding) {
            // UI에 오름 정보 반영
            oreumName.text = oreum?.oreumKname ?: "이름 없음"
            oreumAddress.text = oreum?.oreumAddr ?: "주소 없음"
            oreumImage.load(oreum?.imgPath) {
                memoryCacheKey(oreum?.imgPath)
                diskCacheKey(oreum?.imgPath)
                allowHardware(false)
                crossfade(true)
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.error_image)
            }

            // Fragment 전체 클릭 시 DetailFragment로 이동
            root.setOnClickListener {
                oreum?.let {
                    dismiss()
                    parentFragmentManager.setFragmentResult(
                        "navigateToDetail",
                        Bundle().apply {
                            putParcelable("oreum", it)
                        })
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}