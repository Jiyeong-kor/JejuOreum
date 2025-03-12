package com.jeong.jjoreum.presentation.ui.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.jakewharton.rxbinding4.view.clicks
import com.jeong.jjoreum.databinding.DialogJoinSuccessBinding
import com.jeong.jjoreum.presentation.ui.main.MainActivity
import java.util.concurrent.TimeUnit

/**
 * 회원가입 완료 후 표시되는 성공 다이얼로그
 */
class JoinSuccessDialog : DialogFragment() {
    companion object {
        /**
         * 인스턴스 생성 함수
         * @return JoinSuccessDialog 인스턴스
         */
        fun newInstance() = JoinSuccessDialog()
    }

    private lateinit var binding: DialogJoinSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false // 외부 터치로 닫히지 않도록 설정
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogJoinSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 확인 버튼 클릭 시 메인 화면으로 이동 후 다이얼로그 종료
        binding.joinSuccessBtn.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                dismiss()
                startActivity(Intent(context, MainActivity::class.java))
                requireActivity().finish()
            }
    }
}
