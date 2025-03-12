//package com.jeong.jjoreum.ui.detail.review
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.DialogFragment
//import com.jakewharton.rxbinding4.view.clicks
//import com.jeong.jjoreum.R
//import com.jeong.jjoreum.databinding.DialogReviewBinding
//import java.util.concurrent.TimeUnit
//
///**
// * 리뷰 작성 다이얼로그를 표시하는 예시 코드
// * @constructor 매개변수가 없는 Companion Object newInstance()로 생성
// */
//class ReviewDialog() : DialogFragment() {
//    companion object {
//        fun newInstance() = ReviewDialog()
//    }
//
//    private lateinit var binding: DialogReviewBinding
//    private lateinit var nickname: String
//    private lateinit var oreumName: String
//    private var oreumIdx: Int = -1
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val bundle = arguments
//        nickname = bundle!!.getString("nickname", "")
//        oreumName = bundle.getString("oreumName", "")
//        oreumIdx = bundle.getInt("oreumIdx", -1)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = DialogReviewBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.dialogReviewNickname.text = getString(R.string.dialog_review_nickname, nickname)
//        binding.dialogReviewEdt.hint = getString(R.string.dialog_review_hint, oreumName)
//        binding.dialogReviewBtn.clicks()
//            .throttleFirst(500, TimeUnit.MILLISECONDS)
//            .subscribe {
//                val userReview = binding.dialogReviewEdt.text.toString()
//                val time = System.currentTimeMillis()
//                saveReview(oreumIdx, nickname, userReview, time)
//                dismiss()
//            }
//    }
//}
