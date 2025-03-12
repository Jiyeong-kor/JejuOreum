//package com.jeong.jjoreum.ui.detail.stamp
//
//import android.animation.Animator
//import android.animation.Animator.AnimatorListener
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.DialogFragment
//import com.jakewharton.rxbinding4.view.clicks
//import com.jeong.jjoreum.R
//import com.jeong.jjoreum.databinding.DialogStampBooleanBinding
//import java.util.concurrent.TimeUnit
//
///**
// * 스탬프 인증 성공/실패 여부를 표시하는 다이얼로그 예시 코드
// */
//class StampBooleanDialog : DialogFragment() {
//    companion object {
//        fun newInstance() = StampBooleanDialog()
//    }
//
//    private lateinit var binding: DialogStampBooleanBinding
//    private lateinit var oreumName: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        isCancelable = false
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = DialogStampBooleanBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        // string 설정
//        val bundle = arguments
//        oreumName = bundle!!.getString("oreumName", "")
//        binding.stampTrueText.text = getString(R.string.stamp_true_text, oreumName)
//        binding.stampFalseText.text = getString(R.string.stamp_false_text, oreumName)
//
//        // 스탬프 성공 실패 boolean에 따른 보여줄 화면 설정
//        if (tag == "true") {
//            binding.stampTrue.visibility = View.VISIBLE
//            binding.stampFalse.visibility = View.GONE
//            binding.stampTrueLottie.playAnimation()
//            binding.stampTrueLottie.addAnimatorListener(object : AnimatorListener {
//                override fun onAnimationStart(p0: Animator) {}
//
//                override fun onAnimationEnd(p0: Animator) {
//                    dismiss()
//                }
//
//                override fun onAnimationCancel(p0: Animator) {}
//
//                override fun onAnimationRepeat(p0: Animator) {}
//            })
//
//        } else {
//            binding.stampTrue.visibility = View.GONE
//            binding.stampFalse.visibility = View.VISIBLE
//            binding.stampFalseBtn.clicks()
//                .throttleFirst(500, TimeUnit.MILLISECONDS)
//                .subscribe {
//                    dismiss()
//                }
//        }
//    }
//}
